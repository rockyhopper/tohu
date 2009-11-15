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
package org.tohu.write.questionnaire.helpers;

import java.util.Map;

import org.tohu.domain.questionnaire.Application;
import org.tohu.domain.questionnaire.framework.ConditionConstants;
import org.tohu.domain.questionnaire.framework.PageElementConstants;

public class FieldTypeHelper implements PageElementConstants, ConditionConstants {

	public static String formatOperationString(Application application, String op) {
		if (op.startsWith("\"") && op.endsWith("\"") && (op.length() > 2)) {
			op = op.substring(1, op.length() - 2);
		}
		else {				
			if (op.toUpperCase().startsWith(OP_NOOP_UPPER)) {
				op = "";
			}
			else if (op.toUpperCase().startsWith(OP_IS_NOT_UPPER)) {
				op = "!=";
			}
			else if (op.toUpperCase().startsWith(OP_IS_UPPER)) {
				op = "==";
			}
		}
		return op;
	}

	
	public static String formatValueStringInLogic(Application application, Map<String, String> itemVariables, String valueString) {
		if ((valueString == null) || valueString.toUpperCase().equals(VALUE_EMPTY_UPPER)) {
			return "null";
		}
		if (valueString.indexOf(".") > 0) {
			return valueString;
		}
		String varName = itemVariables.get(valueString);
		if (varName != null) {
			valueString = String.format("%s.answer", varName);
		}
		else if (!valueString.startsWith("\"")) {
			valueString = "\"" + valueString + "\"";
		}
		return valueString;
	}
    
	public static String formatValueStringAccordingToType(String tempStr, String type) {
		if (tempStr == null) {
			return null;
		}
    	if (type.equals(FIELD_TYPE_TEXT) || type.equals(FIELD_TYPE_DATE)) {
    		if (!tempStr.startsWith("\"")) {
    			tempStr = "\"" + tempStr + "\"";
	    	}
    	}
    	else if (type.equals(FIELD_TYPE_NUMBER)){
    		if (!tempStr.endsWith("L")) {
    			tempStr = tempStr + "L";
	    	}
    	}
    	else if (type.equals(FIELD_TYPE_DECIMAL)){
    		if (!tempStr.endsWith("D")) {
    			tempStr = tempStr + "D";
	    	}
    	}
    	return tempStr;
	}

	public static String mapFieldTypeToQuestionType(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return TYPE_TEXT;
		}
		
		if (theFieldType.equals(FIELD_TYPE_BOOLEAN)) {
			return TYPE_BOOLEAN;
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return TYPE_NUMBER;
		}
		
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return TYPE_DECIMAL;
		}
		
		if (theFieldType.equals(FIELD_TYPE_DATE)) {
			return TYPE_DATE;
		}
		
		System.out.println("Converting type: " + theFieldType + " to Text");
		return TYPE_TEXT;
	}
	
	public static String mapFieldTypeToBaseVariableName(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return "textAnswer";
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return "numberAnswer";
		}
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return "decimalAnswer";
		}
		if (theFieldType.equals(FIELD_TYPE_BOOLEAN)) {
			return "booleanAnswer";
		}
		if (theFieldType.equals(FIELD_TYPE_DATE)) {
			return "dateAnswer";
		}
		
		System.out.println("Converting type: " + theFieldType + " to Text");
		return "textAnswer";
	}
	
	public static String mapFieldTypeToJavaClassName(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return "String";
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return "Long";
		}
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return "Double";
		}
		if (theFieldType.equals(FIELD_TYPE_BOOLEAN)) {
			return "Boolean";
		}
		if (theFieldType.equals(FIELD_TYPE_DATE)) {
			return "Date";
		}
		
		System.out.println("Converting type : " + theFieldType + " to Java Class String");
		return "String";
	}
	
	public static String mapFieldTypeToJavaNumberClassMethodName(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return "toString()";
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return "longValue()";
		}
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return "doubleValue()";
		}
		
		System.out.println("Converting number method type: " + theFieldType + " to Text");
		return "toString()";
	}
	

}
