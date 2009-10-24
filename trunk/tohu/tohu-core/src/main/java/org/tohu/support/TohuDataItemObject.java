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

public abstract class TohuDataItemObject implements Serializable, TohuAnswerContainer, TohuAnswerTypes {

	private static final long serialVersionUID = 1L;
	
	private String id;

	/**
	 * This is a way of optionally abstractly grouping elements for use outside Tohu.
	 */
	private String category;

	private TohuAnswerTypeManager answerType;

	private String textAnswer;

	private Long numberAnswer;

	private BigDecimal decimalAnswer;

	private Boolean booleanAnswer;

	private Date dateAnswer;

	public TohuDataItemObject() {
		this(null);
	}

	public TohuDataItemObject(String id) {
		super();
		setId(id);
	}
	
	/**
	 * @see org.tohu.TohuObject#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return an optional abstract grouping identifying string
	 * 
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets an (optional) abstract grouping identifying string.
	 * 
	 * To be used outside of Tohu.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Sets the unique id for this item which must be non-null and cannot contain any commas or dots.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		if (id == null || id.contains(",") || id.contains(".")) {
			throw new IllegalArgumentException("Invalid item id");
		}
		if (this.id != null && !this.id.equals(id)) {
			throw new IllegalStateException("id may not be changed");
		}
		this.id = id;
	}


	public String getAnswerType() {
		if (answerType == null) {
			return null;
		}
		return answerType.getAnswerType();
	}

	public void setAnswerType(String answerType) {
		if (this.answerType == null) {
			this.answerType = new TohuAnswerTypeManager(answerType, this);
		}
		else {
			this.answerType.setAnswerType(answerType);
		}
	}

	/**
	 * Returns the basic answer type.
	 * 
	 * @return
	 */
	public String getBasicAnswerType() {
		if (answerType == null) {
			return null;
		}
		return answerType.getBasicAnswerType();
	}


	public String getTextAnswer() {
		checkType(TYPE_TEXT);
		return textAnswer;
	}

	public void setTextAnswer(String textAnswer) {
		checkType(TYPE_TEXT);
		this.textAnswer = textAnswer;
	}

	public Long getNumberAnswer() {
		checkType(TYPE_NUMBER);
		return numberAnswer;
	}

	public void setNumberAnswer(Long numberAnswer) {
		checkType(TYPE_NUMBER);
		this.numberAnswer = numberAnswer;
	}

	public BigDecimal getDecimalAnswer() {
		checkType(TYPE_DECIMAL);
		return decimalAnswer;
	}

	public void setDecimalAnswer(BigDecimal decimalAnswer) {
		checkType(TYPE_DECIMAL);
		this.decimalAnswer = decimalAnswer;
	}

	public Boolean getBooleanAnswer() {
		checkType(TYPE_BOOLEAN);
		return booleanAnswer;
	}

	public void setBooleanAnswer(Boolean booleanAnswer) {
		checkType(TYPE_BOOLEAN);
		this.booleanAnswer = booleanAnswer;
	}

	public Date getDateAnswer() {
		checkType(TYPE_DATE);
		return dateAnswer;
	}

	public void setDateAnswer(Date dateAnswer) {
		checkType(TYPE_DATE);
		this.dateAnswer = dateAnswer;
	}

	public void setAnswer(Object answer) {
		if (answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		answerType.setAnswer(answer);
	}

	public Object getAnswer() {
		if (answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		return answerType.getAnswer();
	}

	public boolean isAnswered() {
		return getAnswer() != null;
	}

	/**
	 * Checks that the supplied answer type is correct.
	 * 
	 * @param answerType
	 */
	private void checkType(String answerType) {
		if (this.answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		this.answerType.checkType(answerType);
	}

	/**
	 * Clears any previous answer (which may be of a different data type).
	 */
	public void clearAnswer() { 
		textAnswer = null;
		numberAnswer = null;
		decimalAnswer = null;
		booleanAnswer = null;
		dateAnswer = null;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + ": id=" + id + " answerType="
				+ getAnswerType() + " answer=" + getAnswer() + " category=" + getCategory();
	}
	
}
