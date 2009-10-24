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
import java.math.BigDecimal;
import java.util.Date;

import org.tohu.support.TohuAnswerTypes;

/**
 * <p>
 * Represents a Data point that may be represented a number of ways.
 * </p>
 * 
 * <p>
 * <code>DataItem</code> has an <code>answerType</code> which must be one of:
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
 * The answer to a <code>DataItem</code> is maintained internally by the object. use <code>DomainModelAssociation</code> to
 * map the answers to a real domain model.
 * </p>
 * 
 * @author Derek Rendall
 */
public class TohuAnswerTypeManager implements Serializable, TohuAnswerTypes {

	private static final long serialVersionUID = 1L;


	private String answerType;
	private TohuAnswerContainer container;

	public TohuAnswerTypeManager(String answerType, TohuAnswerContainer container) {
		super();
		this.container = container;
		setAnswerType(answerType);
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setAnswerType(String answerType) {
		String previousBasicAnswerType = answerTypeToBasicAnswerType(this.answerType);
		String basicAnswerType = answerTypeToBasicAnswerType(answerType);
		if (basicAnswerType == null
				|| (!basicAnswerType.equals(TYPE_TEXT) && !basicAnswerType.equals(TYPE_NUMBER)
						&& !basicAnswerType.equals(TYPE_DECIMAL) && !basicAnswerType.equals(TYPE_BOOLEAN) && !basicAnswerType
						.equals(TYPE_DATE))) {
			throw new IllegalArgumentException("answerType " + answerType + " is invalid");
		}
		this.answerType = answerType;
		if (!basicAnswerType.equals(previousBasicAnswerType)) {
			container.clearAnswer();
		}
	}

	/**
	 * Returns the basic answer type.
	 * 
	 * @return
	 */
	public String getBasicAnswerType() {
		return answerTypeToBasicAnswerType(answerType);
	}

	protected String answerTypeToBasicAnswerType(String answerType) {
		if (answerType == null) {
			return null;
		}
		int i = answerType.indexOf('.');
		if (i >= 0) {
			return answerType.substring(0, i);
		}
		return answerType;
	}


	public void setAnswer(Object answer) {
		if (answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		String basicAnswerType = getBasicAnswerType();
		if (basicAnswerType.equals(TYPE_TEXT)) {
			container.setTextAnswer((String) answer);
		}
		if (basicAnswerType.equals(TYPE_NUMBER)) {
			container.setNumberAnswer((Long) answer);
		}
		if (basicAnswerType.equals(TYPE_DECIMAL)) {
			container.setDecimalAnswer((BigDecimal) answer);
		}
		if (basicAnswerType.equals(TYPE_BOOLEAN)) {
			container.setBooleanAnswer((Boolean) answer);
		}
		if (basicAnswerType.equals(TYPE_DATE)) {
			container.setDateAnswer((Date) answer);
		}
	}

	/**
	 * Checks that the supplied answer type is correct.
	 * 
	 * @param answerType
	 */
	public void checkType(String answerType) {
		if (this.answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		String basicAnswerType = getBasicAnswerType();
		if (!basicAnswerType.equals(answerType)) {
			throw new IllegalStateException("Supplied answer type " + answerType + " differs from the expected type "
					+ basicAnswerType);
		}
	}
	
	public Object getAnswer() {
		if (answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		String basicAnswerType = getBasicAnswerType();
		if (basicAnswerType.equals(TYPE_TEXT)) {
			return container.getTextAnswer();
		}
		if (basicAnswerType.equals(TYPE_NUMBER)) {
			return container.getNumberAnswer();
		}
		if (basicAnswerType.equals(TYPE_DECIMAL)) {
			return container.getDecimalAnswer();
		}
		if (basicAnswerType.equals(TYPE_BOOLEAN)) {
			return container.getBooleanAnswer();
		}
		if (basicAnswerType.equals(TYPE_DATE)) {
			return container.getDateAnswer();
		}
		throw new IllegalStateException();
	}


	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return " answerType=" + getAnswerType();
	}


}
