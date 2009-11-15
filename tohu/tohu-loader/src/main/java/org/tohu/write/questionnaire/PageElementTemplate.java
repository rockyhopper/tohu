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
package org.tohu.write.questionnaire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.tohu.domain.questionnaire.Application;
import org.tohu.domain.questionnaire.PageElement;
import org.tohu.domain.questionnaire.framework.ConditionConstants;
import org.tohu.domain.questionnaire.framework.ListEntryTuple;
import org.tohu.domain.questionnaire.framework.PageElementConstants;
import org.tohu.write.questionnaire.helpers.FieldTypeHelper;

/**
 * 
 * @author Derek Rendall
 *
 */
public class PageElementTemplate implements PageElementConstants, ConditionConstants {
	
	protected PageElement element = null;
	
	public PageElementTemplate(PageElement element) {
		super();
		this.element = element;
	}
		
	
	protected String checkType(String itemId) {
		if (itemId.equals(ITEM_TYPE_NORMAL_IMPACT)) {
			return ITEM_TYPE_DATA_ITEM;
		}
		return itemId;
	}
	
		
	/**
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	protected void writeFunctionalConsequence(Application application, Formatter fmt) throws IOException {
		// TODO replace Logic Element with PageElementCondition
		if ((element.getLogicElement() == null)) {
			throw new IllegalArgumentException("You cannot have an empty logical element for a functional consequence!");
		}
		
		String functionName = null;
		String opName = element.getLogicElement().getOperation();
		if (opName == null) {
			throw new IllegalArgumentException("You cannot have an empty logical operation for a functional consequence!");
		}
		
		if (opName.equalsIgnoreCase(FUNCTION_MAX)) {
			functionName = FUNCTION_MAX;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_MIN)) {
			functionName = FUNCTION_MIN;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_AVERAGE)) {
			functionName = FUNCTION_AVERAGE;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_SUM)) {
			functionName = FUNCTION_SUM;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_COUNT)) {
			functionName = FUNCTION_COUNT;
		}
		if (functionName == null) {
			throw new IllegalArgumentException("Invalid operation " + opName + " for a functional consequence!");
		}
		
		writeCreationOfAGlobalConsequence(application, fmt);
		
		String tempFactName = "Temp" + element.getId();
	    fmt.format("declare %s\n", tempFactName);
	    fmt.format("\tnumber : Number\n");
	    fmt.format("end\n\n");

	    fmt.format("rule \"Function %s\"\nno-loop\n", element.getId());
	    fmt.format("when\n");
	    fmt.format("\t$total : Number()\n");
	    fmt.format("\t\tfrom accumulate (%s(%s == \"%s\", answered == true, $value : %s),\n %s ( $value ) )\n", 
	    		checkType(element.getLogicElement().getItemId()),
	    		element.getLogicElement().getItemAttribute(),
	    		element.getLogicElement().getValue(),
	    		FieldTypeHelper.mapFieldTypeToBaseVariableName(element.getFieldType()),
	    		functionName);
	    fmt.format("then\n");
	    fmt.format("\t%s temp = new %s();\n", tempFactName, tempFactName);
	    fmt.format("\ttemp.setNumber($total);\n");
	    fmt.format("\tinsert(temp);\n");
	    fmt.format("end\n\n");
		
	    fmt.format("rule \"Assign %s\"\nno-loop\n", element.getId());
	    fmt.format("when\n");
	    fmt.format("\t$impact : %s(id == \"%s\");\n", ITEM_TYPE_DATA_ITEM, element.getId());
	    fmt.format("\t$v : %s();\n", tempFactName);
	    fmt.format("then\n");
	    fmt.format("\t$impact.setAnswer(new %s($v.getNumber().%s));\n", 
	    		FieldTypeHelper.mapFieldTypeToJavaClassName(element.getFieldType()), 
	    		FieldTypeHelper.mapFieldTypeToJavaNumberClassMethodName(element.getFieldType()));
	    fmt.format("\tretract($v);\n");
	    fmt.format("\tupdate($impact);\n");
	    fmt.format("end\n\n");		
	}
	
	/**
	 * @param application
	 * @param fmt
	 * @param showReason
	 * @return
	 * @throws IOException
	 */
	protected String writeCommonFactCreationCode(Application application, Formatter fmt, boolean showReason) throws IOException {
	    String variableName = "a" + element.getType();
	    fmt.format("\t%s %s = new %s(\"%s\");\n", element.getType(), variableName, element.getType(), element.getId());
	    if (element.isAQuestionType() || element.isAConsequenceType()) {
		    String type = FieldTypeHelper.mapFieldTypeToQuestionType(element.getFieldType());
		    fmt.format("\t%s.setAnswerType(%s);\n", variableName, type);
		    if (element.getDefaultValueStr() != null) {
		    	String tempStr = FieldTypeHelper.formatValueStringAccordingToType(element.getDefaultValueStr(), element.getFieldType());
			    fmt.format("\t%s.setAnswer(%s);\n", variableName, tempStr);
		    }
		    if (element.isAQuestionType() && element.isRequired()) {
			    fmt.format("\t%s.setRequired(true);\n", variableName);
		    }
		    if (element.getCategory() != null) {
			    fmt.format("\t%s.setCategory(\"%s\");\n", variableName, element.getCategory());
		    }
		    if (element.isAConsequenceType() && showReason && (element.getPostLabel() != null)) {
			    fmt.format("\t%s.setReason(\"%s\");\n", variableName, element.getPostLabel());
			}
	    }
	    
	    if (element.getPreLabel() != null) {
	    	String methodText = (element.isAQuestionType()) ? "PreLabel" : (element.isAConsequenceType()) ? "Name" : "Label"; 
		    fmt.format("\t%s.set%s(\"%s\");\n", variableName, methodText, element.getPreLabel());
	    }
	    return variableName;
	}

	/**
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	protected void writeCreationOfAGlobalConsequence(Application application, Formatter fmt) throws IOException {
	    fmt.format("rule \"Create %s\"\ndialect \"mvel\"\nno-loop\nsalience 100\n", element.getId());
	    fmt.format("then\n");
	    String variableName = writeCommonFactCreationCode(application, fmt, false);
	    fmt.format("\tinsert(%s);\n", variableName);
	    fmt.format("end\n\n");
	}
	
	/**
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	public void writeValidationDRLFileContents(Application application, Formatter fmt) throws IOException {
	    fmt.format("rule \"Validate %s %d\"\n", element.getId(), element.getRowNumber());
	    fmt.format("when\n");
	    
	    // do when stuff
	    new WhenClauseTemplate(element).writeLogicSectionDRLFileContents(application, fmt, false);
	    String message = element.getPreLabel();
	    element.setPreLabel(null);
	    if (message == null) {
			message = "Invalid value";
			System.out.println("Warning - validation " + element.getId() + " has no validation message defined.");
		}
	    
	    fmt.format("then\n");
	    PageElement baseQuestion = element.findPreviousQuestion();
	    if (baseQuestion == null) {
			throw new IllegalStateException("Validation " + element.getId() + " has no previous question to attach to.");
		}
	    fmt.format("\tinsertLogical(new InvalidAnswer(\"%s\", \"%s\"));\n", baseQuestion.getId(), message);
	    fmt.format("end\n\n");
	}
	    
	
	/**
	 * @param application
	 * @param fmt
	 * @param possibleAnswers
	 * @throws IOException
	 */
	protected void writeSubItems(Application application, Formatter fmt, String variableName, boolean possibleAnswers) throws IOException {
		if (possibleAnswers && ((element.getLookupTable() == null) || (element.getLookupTable().getEntries().size() == 0))) {
			// This is ok for a Lookup Object
			System.out.println("No entries for " + element.getId());
			return;
		}
		List<ListEntryTuple> entries = null;
		if (possibleAnswers) {
			entries = element.getLookupTable().getEntries();
		}
		else {
			if ((element.getChildren() == null) || (element.getChildren().size() == 0)) {
				throw new IllegalStateException("No children for group " + element.getId());
			}
			entries = new ArrayList<ListEntryTuple>();
			for (Iterator<PageElement> i = element.getChildren().iterator(); i.hasNext();) {
				PageElement e = (PageElement) i.next();
				if (e.isARepeatingElement()) {
					PageElement temp = application.findPageElement(e.getId());
					if (temp == null) {
						throw new IllegalArgumentException("A repeating element has no master element for id " + e.getId());
					}
					e = temp;
				}
				if (e.isAGroupType() || e.isAQuestionType() || e.isANoteType()) {
					entries.add(new ListEntryTuple(e.getId()));
				}
			}
			if (entries.size() == 0) {
				throw new IllegalStateException("No group, note or question children for group " + element.getId());
			}
		}
		
	    fmt.format("\t%s.", variableName);
		boolean firstOne = true;
		boolean onlyOne = entries.size() == 1;
		String indent = (onlyOne) ? "" : "\t\t";
		String newLine = (onlyOne) ? "" : "\n";
		
		if (possibleAnswers) {
		    fmt.format("setPossibleAnswers({");
		}
		else {
		    fmt.format("setItems({");
		}

		boolean wroteOne = false;
		for (Iterator<ListEntryTuple> i = entries.iterator(); i.hasNext();) {
			ListEntryTuple tuple = (ListEntryTuple) i.next();
			if (possibleAnswers && (tuple.getConditionClause() != null)) {
				continue;
			}
			wroteOne = true;
			if (firstOne) {
				firstOne = false;
				fmt.format("%s", newLine);
			}
			else {
				fmt.format(",%s", newLine);
			}
			String idStr = tuple.getId();
			if ((idStr != null) && (!idStr.startsWith("\""))) {
				idStr = "\"" + idStr + "\"";
			}
			
			if (possibleAnswers) {
				String rep = tuple.getRepresentation();
				if ((rep != null) && (!rep.startsWith("\""))) {
					rep = "\"" + rep + "\"";
				}
				if (tuple.getId() == null) {
					fmt.format("%snew PossibleAnswer(null, %s)", indent, rep);
					continue;
				}
				if (rep == null) {
					fmt.format("%snew PossibleAnswer(%s)", indent, idStr);
				}
				else {
					fmt.format("%snew PossibleAnswer(%s, %s)", indent, idStr, rep);
				}
			}
			else {
				fmt.format("%s%s", indent, idStr);
			}
		}
		if (!wroteOne) {
			// TODO handle case where all elements are controlled by logic?
			throw new IllegalStateException("None of the items/possible answers for " + element.getId() + " were created as a default list. Mke sure at least one entry does not have a logic elements associated with it.");
		}
	    fmt.format("});\n");
	}

	/**
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	protected void writeInitiateBranchPageDRLFileContents(Application application, Formatter fmt) throws IOException {
		if (!element.isABranchedPage()) throw new IllegalArgumentException("Cannot process a normal page in writeInitiateBranchPageDRLFileContents :" + element.getId());
				
	    fmt.format("rule \"Branch %s\"\n", element.getId());
	    fmt.format("salience 75\n");
	    fmt.format("no-loop\n");
	    fmt.format("when\n");
	    
	    // do when stuff
	    new WhenClauseTemplate(element).writeLogicSectionDRLFileContents(application, fmt, true);
	    
		// TODO support more than one level of branching
		fmt.format("\tq : Questionnaire(items not contains \"%s\");\n", element.getId());
			    
	    // do then stuff
	    fmt.format("then\n");
	    	
    	String insertAfter = "q.getActiveItem()";
    	
    	String displayAfter = element.getPostLabel();
    	element.setPostLabel(null);
    	
    	if (displayAfter != null) {
	    	if (!displayAfter.startsWith("\"")) {
	    		displayAfter = "\"" + displayAfter + "\"";
	    	}
	    	insertAfter = displayAfter;
    	}
		fmt.format("\tif (q.isBranched() == false) {\n\t\tq.navigationBranch(new String[]{\"%s\"}, \"%s\");\n", element.getId(), element.getId());
		fmt.format("\t}\n\telse {\n\t\tq.appendItem(\"%s\", %s);\n\t}\n", element.getId(), insertAfter);
    	fmt.format("\tupdate(q);\n");

	    fmt.format("end\n\n");
	    
	    
//	    // Now deal with removing page entries, if this is a page display logic element
//	    // BRANCH ITEM IS REMOVED AUTOMATICALLY, but still need to remove fact
//	    fmt.format("rule \"%s Exited Branch %s\"\n", idPrefix, id);
//	    fmt.format("salience 15\n");
//	    fmt.format("no-loop\n");
//	    fmt.format("when\n");
//		// TODO support more than one level of branching
//	    // Ideally should be matching on the keyPageElementId of the DisplayFact
//	    // would need to return the first pe1 element from writeLogicSectionDRLFileContents
//    	fmt.format("\td : DisplayFact(id == \"%s\", $ruleName : ruleName);\n", id);
//    	fmt.format("\tq : Questionnaire(branched == false);\n");
//	    fmt.format("then\n");
//	    fmt.format("\tretract(d);\n");
//	    fmt.format("end\n\n");
	}
	
	/**
	 * The entry point for writing the element to file
	 * 
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	public void writeDRLFileContents(Application application, Formatter fmt) throws IOException {
	    if (element.isARepeatingElement()) {
	    	System.out.println("Repeating item: " + element.getId());
    		// should have already been defined - don't want it defined again - just
    		// referred to again in the parent element, which should have already been done
    		return;
	    }
    	//System.out.println("Processing item: " + this.getId());
	    
	    if (element.isAFunctionConsequenceItem()) {
	    	//System.out.println("Functional Consequence");
	    	writeFunctionalConsequence(application, fmt);
	    	return;
	    }
	    
	    if (element.isAnAlternateConsequenceItem()) {
	    	//System.out.println("Alternate Consequence");
	    	if (application.addNewAlternateConsequence(element.getId())) writeCreationOfAGlobalConsequence(application, fmt);
	    }
	    else if (element.isAConsequenceType() && (element.getLogicElement() == null)) {
	    	writeCreationOfAGlobalConsequence(application, fmt);
	    	return;
	    }
	    else if (element.isAValidationElement()) {
	    	writeValidationDRLFileContents(application, fmt);
	    	return;
	    }
	    
	    if (element.isABranchedPage()) {
	    	writeInitiateBranchPageDRLFileContents(application, fmt);
	    }
	    
	    String ruleName = element.getId();
//	    if (element.isAnAlternateConsequenceItem() && (consequenceFacts.size() > 0)) {
//	    	ruleName = ruleName + String.valueOf(consequenceFacts.get(0).getRowNumber());
//	    }
	    if (element.isAnAlternateConsequenceItem()) {
	    	ruleName = ruleName + String.valueOf(element.getRowNumber());
	    }
	    
	    fmt.format("rule \"%s\"\ndialect \"mvel\"\nno-loop\n", ruleName);
	    
	    boolean useGroupIds = ((element.getGroupIds() != null) && (element.getGroupIds().size() > 0)) && (element.isAQuestionType() || !element.isRequired());
	    if (useGroupIds || (element.getDisplayCondition() != null)) {
		    fmt.format("when\n");
		    if (useGroupIds) {
			    fmt.format("\t$group : Group (");
		    	for (Iterator<String> i = element.getGroupIds().iterator(); i.hasNext();) {
					String id = (String) i.next();
			    	if (!id.startsWith("\"")) {
			    		id = "\"" + id + "\"";
			    	}
				    fmt.format("id == %s%s", id, i.hasNext() ? " || " : "");
				}
			    fmt.format(");\n");
		    }

		    if (element.getDisplayCondition() != null) {
		    	new WhenClauseTemplate(element).writeLogicSectionDRLFileContents(application, fmt, false);
		    }
	    }
	    

	    if (element.isAnAlternateConsequenceItem()) {
		    fmt.format("\taDataItem : %s (id == \"%s\")\n", ITEM_TYPE_DATA_ITEM, element.getId());
		    fmt.format("then\n");
	    	String tempStr = FieldTypeHelper.formatValueStringAccordingToType(element.getDefaultValueStr(), element.getFieldType());
		    fmt.format("\taDataItem.setAnswer(%s);\n", tempStr);
		    fmt.format("\tupdate(aDataItem);\n");
		}
	    else {
		    fmt.format("then\n");
	    
		    String variableName = writeCommonFactCreationCode(application, fmt, true);
		    
		    if (!element.isAConsequenceType()) {
			    if (element.getPostLabel() != null) {
				    fmt.format("\t%s.setPostLabel(\"%s\");\n", variableName, element.getPostLabel());
			    }
			    if ((element.getType().equals(ITEM_TYPE_MULTI_CHOICE_Q)) || (element.getType().equals(ITEM_TYPE_GROUP))) {
				    writeSubItems(application, fmt, variableName, element.getType().equals(ITEM_TYPE_MULTI_CHOICE_Q));
			    }
			    if (!element.getStyles().isEmpty()) {
					boolean firstOne = true;
					boolean onlyOne = element.getStyles().size() == 1;
					String indent = (onlyOne) ? "" : "\t\t";
					String newLine = (onlyOne) ? "" : "\n";
				    fmt.format("\t%s.setPresentationStyles({", variableName);
					for (Iterator<String> i = element.getStyles().iterator(); i.hasNext();) {
						String style = (String) i.next();
						if (firstOne) {
							firstOne = false;
							fmt.format("%s", newLine);
						}
						else {
							fmt.format(",%s", newLine);
						}
						if ((style != null) && (!style.startsWith("\""))) {
							style = "\"" + style + "\"";
						}
						
						fmt.format("%s%s", indent, style);
					}
				    fmt.format("});\n");
				}
		    }
		    
		    fmt.format("\tinsertLogical(%s);\n", variableName);
	    }
	    
	    fmt.format("end\n\n");
	    
	    
//	    for (Iterator<DisplayFact> i = displayFacts.iterator(); i.hasNext();) {
//	    	DisplayFact fact = (DisplayFact) i.next();
//			fact.writeDRLFileContents(application, fmt);
//		}
//	    
//	    for (Iterator<ConsequenceFact> i = consequenceFacts.iterator(); i.hasNext();) {
//	    	ConsequenceFact fact = (ConsequenceFact) i.next();
//			fact.writeDRLFileContents(application, fmt);
//		}
	    
	}


}
