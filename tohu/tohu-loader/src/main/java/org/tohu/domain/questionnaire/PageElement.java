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
package org.tohu.domain.questionnaire;

import java.util.ArrayList;
import java.util.List;

import org.tohu.domain.questionnaire.conditions.ConditionClause;
import org.tohu.domain.questionnaire.conditions.PageElementCondition;
import org.tohu.domain.questionnaire.framework.PageElementConstants;

/**
 * 
 * @author Derek Rendall
 *
 */
public class PageElement implements PageElementConstants {

		
	private String id;
	private List<String> groupIds = null;
	private String type;
	private String lookupTableId;
	private boolean required = false;
	private String fieldType;
	private String preLabel;
	private String postLabel;
	private String defaultValueStr;
	private List<String> styles = new ArrayList<String>();
	private LookupTable lookupTable = null;
	private boolean aPageElement;
	private String pageType = null;
	private String spreadsheetType = null;
	private PageElementCondition displayCondition = null;
	//private List<PageElementCondition> validationConditions = new ArrayList<PageElementCondition>();
	private PageElementCondition currentValidationCondition = null;
	private String category = null;
	private int depth;
	private int rowNumber;
	
	private ConditionClause logicElement = null;
	
	protected List<PageElement> children = new ArrayList<PageElement>();
	protected PageElement parent = null;
	protected PageElement previousSibling = null;
	
	
	public PageElement() {
		super();
	}

	public void setId(String id, int depth, int rowNumber) {
		id = id.trim();
		if (id.indexOf(" ") >= 0) {
			throw new IllegalArgumentException("You cannot have a space in an id [" + id + "]");
		}
		this.id = id;
		this.depth = depth;
		this.rowNumber = rowNumber;
	}

	public PageElement getPreviousSibling() {
		return previousSibling;
	}

	public void setPreviousSibling(PageElement previousSibling) {
		this.previousSibling = previousSibling;
	}

	public boolean isAFunctionConsequenceItem() {
		return (spreadsheetType != null) && (spreadsheetType.equalsIgnoreCase(ITEM_TYPE_FUNCTION_IMPACT));
	}

	public boolean isAnAlternateConsequenceItem() {
		return (spreadsheetType != null) && (spreadsheetType.equalsIgnoreCase(ITEM_TYPE_ALTERNATE_IMPACT));
	}

	public boolean isAQuestionType() {
		if (getType().equals(ITEM_TYPE_QUESTION) || (getType().equals(ITEM_TYPE_MULTI_CHOICE_Q))) {
			return true;
		}
		return false;
	}
	
	public String getSpreadsheetType() {
		return spreadsheetType;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setType(String type) {
		String tempType = type.toUpperCase();
		spreadsheetType = type;
		if (tempType.equals("PAGE")) {
			aPageElement = true;
			this.type = "Group";
			pageType = "Normal";
		}
		else if (tempType.equals("BRANCH")) {
			aPageElement = true;
			this.type = "Group";
			pageType = "Branch";
		}
		else if (tempType.equalsIgnoreCase(ITEM_TYPE_NORMAL_IMPACT) || tempType.equalsIgnoreCase(ITEM_TYPE_FUNCTION_IMPACT) || tempType.equalsIgnoreCase(ITEM_TYPE_ALTERNATE_IMPACT)) {
			this.type = ITEM_TYPE_DATA_ITEM;
		}
		else {
			this.type = type;
		}
	}

	public PageElementCondition getDisplayCondition() {
		return displayCondition;
	}

	public void setDisplayCondition(PageElementCondition displayCondition) {
		this.displayCondition = displayCondition;
	}

	public boolean isAGroupType() {
		if (getType().equals(ITEM_TYPE_GROUP)) {
			return true;
		}
		return false;
	}
	
	public boolean isANoteType() {
		if (getType().equals(ITEM_TYPE_NOTE)) {
			return true;
		}
		return false;
	}
	
	public boolean isAConsequenceType() {
		if (getType().equals(ITEM_TYPE_DATA_ITEM)) {
			return true;
		}
		return false;
	}
	
	public boolean isAPageElement() {
		return aPageElement;
	}
	
	public boolean isABranchedPage() {
		return aPageElement && pageType.equals("Branch");
	}

	public int getDepth() {
		return depth;
	}

	public String getPageType() {
		return pageType;
	}
	
	public PageElement findPreviousQuestion() {
		if (getPreviousSibling() != null) {
			if (getPreviousSibling().isAQuestionType()) {
				return getPreviousSibling();
			}
			return getPreviousSibling().findPreviousQuestion();
		}
		if (getParent() != null) {
			if (getParent().isAQuestionType()) {
				return getParent();
			}
			return getParent().findPreviousQuestion();
		}
		return null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<PageElement> getChildren() {
		return children;
	}
	

	public String getLookupTableId() {
		return lookupTableId;
	}

	public void setLookupTableId(String lookupTableId) {
		this.lookupTableId = lookupTableId;
	}

	public void addChild(PageElement child) {
		//System.out.println("Adding child " + child.getId() + child.getDepth() + " to " + getId() + getDepth());
		if (children.size() > 0) {
			child.setPreviousSibling(children.get(children.size() - 1));
		}
		this.children.add(child);
		child.setParent(this);
	}

	protected void setParent(PageElement parent) {
		this.parent = parent;
	}

	public PageElement getParent() {
		return parent;
	}

	public LookupTable getLookupTable() {
		return lookupTable;
	}

	public void setLookupTable(LookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDefaultValueStr() {
		return defaultValueStr;
	}

	public List<String> getStyles() {
		return styles;
	}

//	public List<PageElementCondition> getValidationConditions() {
//		return validationConditions;
//	}
//
//	public void addValidationCondition(PageElementCondition newCondition) {
//		validationConditions.add(newCondition);
//		currentValidationCondition = newCondition;
//	}

	public PageElementCondition getCurrentValidationCondition() {
		return currentValidationCondition;
	}

	public ConditionClause getLogicElement() {
		return logicElement;
	}
	
	public void setLogicElement(ConditionClause substituteLogicElement) {
		if (logicElement != null) {
			throw new IllegalArgumentException("You cannot replace the logic element");
		}
		logicElement = substituteLogicElement;
	}
	
	public void setLogicDependsOnItemId(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setItemId(value);
	}

	public void setLogicAttribute(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setItemAttribute(value);
	}

	public void setLogicOperation(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setOperation(value);
	}

	public void setLogicValue(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setValue(value);
	}

	public void addGroupId(String groupId) {
		if (groupIds == null) {
			groupIds = new ArrayList<String>();
		}
		if (groupIds.contains(groupId)) {
			return;
		}
		groupIds.add(groupId);
	}

	public String getPreLabel() {
		return preLabel;
	}

	public void setPreLabel(String preLabel) {
		this.preLabel = preLabel;
	}

	public String getPostLabel() {
		return postLabel;
	}

	public void setPostLabel(String postLabel) {
		this.postLabel = postLabel;
	}

	public boolean isARepeatingElement() {
		if ((getType() == null) || (!getType().equals(ITEM_TYPE_REUSE))) {
			return false;
		}
		return true;
	}

	public boolean isAValidationElement() {
		if ((getType() == null) || (!getType().equals(ITEM_TYPE_VALIDATION))) {
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setRequired(String requiredStr) {
		if ((requiredStr != null) && (requiredStr.toUpperCase().startsWith("Y"))) {
			this.required = true;
		}
		else {
			this.required = false;
		}
	}

	public void setDefaultValueStr(String defaultValueStr) {
		this.defaultValueStr = defaultValueStr;
	}
	
	public void addStyle(String style) {
		styles.add(style);
	}
	
	
	
}
