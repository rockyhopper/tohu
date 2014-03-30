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
package org.tohu.xml.event;

import org.kie.api.event.rule.ObjectDeletedEvent;

/**
 * @author Damon Horrell
 */
public class ObjectDeletedEventMock extends WorkingMemoryEventMock implements ObjectDeletedEvent {

	private Object oldObject;

	public ObjectDeletedEventMock(String factHandle, Object oldObject) {
		super(factHandle);
		this.oldObject = oldObject;
	}

	/**
	 * @see org.kie.api.event.rule.ObjectDeletedEvent#getOldObject()
	 */
	public Object getOldObject() {
		return oldObject;
	}

}
