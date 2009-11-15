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
package org.tohu.domain.questionnaire.framework;

import org.tohu.domain.questionnaire.conditions.ConditionClause;

/**
 * 
 * @author Derek Rendall
 *
 */
public class ListEntryTuple {
	private String id = null;
	private String representation;
	private ConditionClause clause;
	
	public ListEntryTuple(String id) {
		this(id, null, null);
	}
	public ListEntryTuple(String id, ConditionClause clause) {
		this(id, null, clause);
	}
	public ListEntryTuple(String id, String representation) {
		this(id, representation, null);
	}
	
	public ListEntryTuple(String id, String representation, ConditionClause clause) {
		super();
		this.id = id;
		this.representation = representation;
		this.clause = clause;
	}
	
	public String getId() {
		return id;
	}
	public String getRepresentation() {
		return representation;
	}
	public ConditionClause getConditionClause() {
		return clause;
	}
	
}

