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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * Represents a consequence of actions and data entered by a user.
 * </p>
 * 
 * <p>
 * <code>Consequence</code> has an <code>answerType</code> which must be one of:
 * </p>
 * 
 * <ul>
 * <li><code>text</code></li>
 * <li><code>number</code></li>
 * <li><code>decimal</code></li>
 * <li><code>boolean</code></li>
 * <li><code>date</code></li>
 * </ul>
 * 
 * <p>
 * or an extension of one of these using the notation <code>&lt;type&gt;.&lt;extension type&gt; </code> e.g.
 * <code>text.url</code> or <code>decimal.currency</code>.
 * </p>
 * 
 * <p>
 * The answer to a <code>Consequence</code> is maintained internally by the object. use <code>DomainModelAssociation</code> to
 * map the answers to a real domain model.
 * </p>
 * 
 * @author Derek Rendall
 */
public class Consequence extends Variable {

	private static final long serialVersionUID = 1L;

	private String name;
		
	private List<String> reasons = new ArrayList<String>();

	public Consequence() {
		super();
	}

	public Consequence(String id) {
		super(id);
	}

	public Consequence(String id, String name) {
		super(id, name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	
	/**
	 * Gets list of reasons.
	 * 
	 * @return
	 */
	public List<String> getReason() {
		return reasons.isEmpty() ? null : reasons;
	}

	/**
	 * <p>
	 * Sets the list of reasons for this consequence.
	 * </p>
	 * 
	 * <p>
	 * <code>reasons</code> are used to show why this consequence occurred.
	 * </p>
	 * 
	 * @param reasons
	 */
	public void setReasons(String[] reasons) {
		if (reasons == null) {
			this.reasons = new ArrayList<String>();
		} else {
			for (int i = 0; i < reasons.length; i++) {
				if (reasons[i] != null) {
					this.reasons.add(reasons[i]);
				}
			}
		}
	}

	/**
	 * Sets list of reasons.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * <p>
	 * <code>item.setReasons({"a", "b"});</code>
	 * </p>
	 * 
	 * @param reasons
	 */
	public void setReasons(Object[] reasons) {
		if (reasons == null || reasons.length == 0) {
			this.reasons = new ArrayList<String>();
		} else {
			setReasons((String[]) Arrays.asList(reasons).toArray(new String[] {}));
		}
	}

	/**
	 * Adds a reason to the list. Duplicates and nulls ignored.
	 * 
	 * @param reason
	 */
	public void addReason(String reason) {
		if (reason != null) {
			if (this.reasons == null) {
				this.reasons = new ArrayList<String>();
			}
			if (!this.reasons.contains(reason)) {
				this.reasons.add(reason);
			}
		}
	}

	/**
	 * Sets the reason list to be this reason. Nulls will empty the list.
	 * Useful when using reasons from a display fact but do not want to
	 * manage the reason list contents via truth maintenance.
	 * 
	 * @param reason
	 */
	public void setReason(String reason) {
		if ((reason != null) && (reason.equals("null"))) {
			reason = null;
		}
		if (reason != null) {
			if (this.reasons == null) {
				this.reasons = new ArrayList<String>();
			}
			else {
				this.reasons.clear();
			}
			this.reasons.add(reason);
		}
		else if (!(this.reasons == null)) {
			this.reasons.clear();
		}
	}

	/**
	 * Removes a reason from the list. If it was the only one, the list will be set to empty.
	 * 
	 * @param reason
	 */
	public void removeReason(String reason) {
		if (reason != null) {
			reasons.remove(reason);
		}
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return super.toString() + " name=" + getName();
	}


}
