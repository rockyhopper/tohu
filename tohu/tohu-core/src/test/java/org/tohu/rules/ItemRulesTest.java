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
package org.tohu.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.ConsequenceException;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tohu.Note;

/**
 * @author Damon
 * 
 */
public class ItemRulesTest {

	private static final Logger logger = LoggerFactory.getLogger(ItemRulesTest.class);

	private KnowledgeBase knowledgeBase;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/tohu/Item.drl"), ResourceType.DRL);
		if (knowledgeBuilder.hasErrors()) {
			logger.debug(Arrays.toString(knowledgeBuilder.getErrors().toArray()));
		}
		assertFalse(knowledgeBuilder.hasErrors());
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
	}

	@Test
	public void testUniqueItemId() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Note note1 = new Note("note");
			Note note2 = new Note("note");
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.fireAllRules();
			fail();
		} catch (ConsequenceException e) {
			if (e.getCause() instanceof IllegalStateException) {
				if (((IllegalStateException) e.getCause()).getMessage().equals("Duplicate item id: note")) {
					return;
				}
			}
			fail();
		} finally {
			knowledgeSession.dispose();
		}
	}
}
