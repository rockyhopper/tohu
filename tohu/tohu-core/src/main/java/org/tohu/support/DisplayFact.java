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
package org.tohu.support;

import java.io.Serializable;

/**
 * An object representing the decision to display an item.
 * 
 * Truth maintenance will automatically retract the fact when the base conditions disappear.
 * 
 * The Display Facts are primarily used in the Spreadsheet Loader to simplify the logic around 
 * conditionally displaying items. By using a specific Fact, the logic required can be separated from
 * the use of the logic. The fact can be inserted multiple times (different sets of conditions), effectively
 * giving an OR type behaviour.
 * 
 * @author Derek Rendall
 */

public class DisplayFact implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Identifier for this fact. May be reused on multiple facts if there 
	 * are multiple reasons to display an item
	 */
	private String id;
	
	/**
	 * An optional identifier to locate the rule that triggered this fact
	 */
	private String ruleName;
	
	/**
	 * When we are basing the display rule on the value in a Question based item, this
	 * field will contain the item id for that question. Could be used to enable
	 * nested branching capabilities.
	 */
	private String keyPageElementId;
	
	/**
	 * Primarily used by consequence related "display" facts.
	 */
	private String reason;
	
	/**
	 * Normal constructor.
	 * 
	 * @param id
	 */
	public DisplayFact(String id) {
		this(id, null);
	}
	
	/**
	 * Use when want to record what rule triggered this display fact - primarily used by
	 * drl generation engines (such as spreadsheet loader)
	 * 
	 * @param id
	 * @param ruleName
	 */
	public DisplayFact(String id, String ruleName) {
		this(id, ruleName, null);
	}

	/**
	 * Method to use when creating a DisplayFact (condition) for a Branched page.
	 * 
	 * @param id
	 * @param ruleName
	 * @param keyPageElementId
	 */
	public DisplayFact(String id, String ruleName, String keyPageElementId) {
		super();
		this.id = id;
		this.ruleName = ruleName;
		this.keyPageElementId = keyPageElementId;
	}

	public String getId() {
		return id;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getKeyPageElementId() {
		return keyPageElementId;
	}

	public void setKeyPageElementId(String keyPageElementId) {
		this.keyPageElementId = keyPageElementId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	

}
