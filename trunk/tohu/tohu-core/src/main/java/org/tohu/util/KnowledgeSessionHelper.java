/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tohu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.ClassObjectFilter;
import org.drools.common.EqualityKey;
import org.drools.common.InternalFactHandle;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.tohu.Answer;
import org.tohu.Question;
import org.tohu.Questionnaire;
import org.tohu.TohuObject;

/**
 * TODO javadoc
 * 
 * @author Damon Horrell
 */
public class KnowledgeSessionHelper {

	private StatefulKnowledgeSession knowledgeSession;

	public KnowledgeSessionHelper(StatefulKnowledgeSession knowledgeSession) {
		this.knowledgeSession = knowledgeSession;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param knowledgeSession
	 * @return
	 */
	public Questionnaire getQuestionnaire() {
		return (Questionnaire) knowledgeSession.getObjects(new ClassObjectFilter(Questionnaire.class)).toArray()[0];
	}

	/**
	 * Returns a map of question ids and answers sorted by question id.
	 * 
	 * @return
	 */
	public SortedMap<String, Object> getAnswers() {
		SortedMap<String, Object> answers = new TreeMap<String, Object>();
		QueryResults queryResults = knowledgeSession.getQueryResults("answeredQuestions");
		for (Iterator<QueryResultsRow> iterator = queryResults.iterator(); iterator.hasNext();) {
			QueryResultsRow row = iterator.next();
			Question question = (Question) row.get("question");
			answers.put(question.getId(), question.getAnswer());
		}
		return answers;
	}

	/**
	 * Returns a map of the active objects keyed and sorted by id.
	 * 
	 * @return
	 */
	public SortedMap<String, TohuObject> getActiveObjects() {
		SortedMap<String, TohuObject> activeObjects = new TreeMap<String, TohuObject>();
		QueryResults queryResults = knowledgeSession.getQueryResults("activeObjects");
		for (Iterator<QueryResultsRow> iterator = queryResults.iterator(); iterator.hasNext();) {
			QueryResultsRow row = iterator.next();
			TohuObject object = (TohuObject) row.get("object");
			activeObjects.put(object.getId(), object);
		}
		return activeObjects;
	}

	/**
	 * Returns a collection of everything in the working memory which should be persisted.
	 * 
	 * It excludes any objects which were created with insertLogical since they would lose their truth maintenance if reloaded. An Answer
	 * object is returned for each Question which was logically inserted. Other Items shouldn't need to be persisted as the rules should
	 * recreate them again.
	 * 
	 * To use this correctly you need to ensure that your rules use "insert" for everything that needs to be persisted other than answers to
	 * questions. You should use "insertLogical" for anything that doesn't need to be persisted, or in fact must not be persisted. Think
	 * about what is data and what are surrounding controls that may need to be extended later. If you persist more than just data then if
	 * your app changes you won't be able to reload old persisted state. e.g. if you add a new Note to a Group, it won't appear for old data
	 * if you had persisted that Group.
	 * 
	 * @return
	 */
	public Collection<Object> getPersistentObjects() {
		Collection<Object> objects = new ArrayList<Object>();
		for (FactHandle factHandle : knowledgeSession.getFactHandles()) {
			InternalFactHandle internalFactHandle = (InternalFactHandle) factHandle;
			Object object = internalFactHandle.getObject();
			boolean insertedLogically = internalFactHandle.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED;
			if (!insertedLogically) {
				objects.add(object);
			} else if (object instanceof Question) {
				Question question = (Question) object;
				Object answer = question.getAnswer();
				objects.add(new Answer(question.getId(), answer == null ? null : answer.toString()));
			}
		}
		return objects;
	}
}
