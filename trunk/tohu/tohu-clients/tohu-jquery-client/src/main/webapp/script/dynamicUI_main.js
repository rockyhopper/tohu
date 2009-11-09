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
 *
 * @author John Bebbington
 *
 *
 * Main Javascript for JQuery Dynamic UI.
 */

// ID of the root HTML element in which the questionnaire is built. 
var htmlRootID = null;

// Base URL for the completion action.
var actionURL = null;

// Optional ID of a hidden input to store the isGUIBusy flag in so the selenium test client can see it.
var isGUIBusyID = null;;

/**
 * Sets the Action URL.
 * 
 * @param url URL string.
 */
function setActionURL(url) {
	actionURL = url;
}

/**
 * Sets the isGUIBusy ID.
 * 
 * @param id string.
 */
function setIsGUIBusyID(id) {
	isGUIBusyID = id;
}

/**
 * Initializes the GUI configuration based on the calling URL, unless specifically overridden by
 * the calling page.
 * 
 * @param rootID ID of the HTML root element.
 * @param defaultKnowledgebase String The default Knowledgebase to use.
 */
function initializeGUI(rootID, defaultKnowledgebase) {
	if (getDebugFlag() == null) {
		// debugFlag not set, use default.
		if (jQuery.url.param("debug") == "true") {
			setDebugFlag(true);
		}
		else {
			setDebugFlag(false);
		}
	}
	debug("initializeGUI() rootID=" + rootID + " defaultKnowledgebase=" + defaultKnowledgebase);
	
	htmlRootID = rootID;

	var baseURL = jQuery.url.attr("protocol") + "://" + jQuery.url.attr("host") + ":"
			+ jQuery.url.attr("port") + "/" + jQuery.url.segment(0);
	debug("initializeGUI() baseURL=" + baseURL);
	
	if (getDroolsURL() == null) {
		// Drools URL not set, use default.
		if (jQuery.url.param("knowledgebase") != null) {
			setDroolsURL(baseURL + "/stateful/" + jQuery.url.param("knowledgebase"));
		}
		else if (!isNull(defaultKnowledgebase)) {
			setDroolsURL(baseURL + "/stateful/" + defaultKnowledgebase);
		}
		else {
			setDroolsURL(baseURL + "/stateful/default");
		}
	}
	
	if (actionURL == null) {
		// Action URL not set, use default.
		actionURL = baseURL + "/action/";
	}

	if (isGUIBusyID == null) {
		// isGUIBusyID not set, use default.
		isGUIBusyID = jQuery.url.param("isGUIBusyID");
	}

}

/**
 * Called when the page loads.
 * 
 * @param rootID ID of the HTML root element.
 * @param defaultKnowledgebase String The default Knowledgebase to use.
 */
function onQuestionnaireLoad(rootID, defaultKnowledgebase) {
	guiBusy("load");
	initializeGUI(rootID, defaultKnowledgebase);
	debug("onQuestionnaireLoad() rootID=" + rootID + " defaultKnowledgebase=" + defaultKnowledgebase);
	
	// Do application-specific pre processing, if any.
	if (window.preQuestionnaireLoad) {
		preQuestionnaireLoad(rootID, defaultKnowledgebase);
	}

	refreshScreen(getInitialQuestionnaire(), null, null);

	// Do application-specific post processing, if any.
	if (window.postQuestionnaireLoad) {
		postQuestionnaireLoad(rootID, defaultKnowledgebase);
	}

	guiReady();
}

/**
 * Refresh the screen based on the response to an AJAX request to the Drools Execution Server.
 * 
 * @param resultSet ResultSetObject returned from the interface module.
 * @param lastFocusID ID of the question which last had focus.
 * @param tabForward Boolean (true = tabbed forward, false = tabbed backward, null = didn't tab out). 
 */
function refreshScreen(resultSet, lastFocusID, tabForward) {
	debug("refreshScreen() lastFocusID=" + lastFocusID + " tabForward=" + tabForward);

	// Do application-specific pre processing, if any.
	if (window.preRefreshScreen) {
		preRefreshScreen(resultSet, lastFocusID);
	}

	processDeleteList(resultSet.deleteList);
	processUpdateList(resultSet.updateList);
	processCreateList(resultSet.createList);

	// If we tabbed out, explicitly set focus to the next or previous element because it may have
	// changed if we have added or removed questions.
	var focusElement = getNextFocusElement(lastFocusID, tabForward);
	if (focusElement != null) {
		// Make sure the current frame has focus.
		if (top.focus) {
			top.focus();
		}
		
		// Need to pause for .1 of a second before setting focus, otherwise the carat disappears
		// in Firefox for some reason.
		// TODO: Find a better way to do this.
		setTimeout(function () {
			debug("refreshScreen() setting focus on input=" + focusElement.attr("id"));
			focusElement.focus();
			if (focusElement.val() && focusElement.val().length > 0) {
				focusElement.select();
			}
		}, 100);
	}
	
	// Do application-specific post processing, if any.
	if (window.postRefreshScreen) {
		postRefreshScreen(resultSet, lastFocusID);
	}
}

/**
 * Remove all HTML elements in the delete list.
 * 
 * @param theList Array of DeleteObjects.
 */
function processDeleteList(theList) {
	for (var i = 0; i < theList.length; i++) {
		var obj = theList[i];
		// Do application-specific pre processing, if any.
		if (window.preProcessDelete) {
			preProcessDelete(obj);
		}
		
		deleteAnyObjectType(obj);
		if (obj.error) {
			if (getJQElement(obj.id + "_errors").children().length == 0) {
				// Removed last error, remove error class from primary question div.
				getJQElement(obj.id).removeClass("error");
			}
		}
		
		// Do application-specific post processing, if any.
		if (window.postProcessDelete) {
			postProcessDelete(obj);
		}
	}
}

/**
 * Create all HTML elements in the create list.
 * 
 * @param theList Array of fact objects.
 */
function processCreateList(theList) {
	for (var i = 0; i < theList.length; i++) {
		var obj = theList[i];
		debugObject("processCreateList() obj=", obj);
		// Can only create an element if we know where it is in the hierarchy.
		if (obj.hierarchy != null) {
			// Do application-specific pre processing, if any.
			if (window.preProcessCreate) {
				preProcessCreate(obj);
			}
			
			switch (obj.objType) {
			case QUESTIONNAIRE_OBJECT:
				obj.hierarchy.parentID = htmlRootID;
				obj.jq = createQuestionnaire(obj, isGUIBusyID);
				obj.hierarchy.parentID = obj.id + "_form";
				createGroup(obj);
				createControls(obj);
				break;
			case GROUP_OBJECT:
				obj.hierarchy.parentID = obj.hierarchy.parentID + "_items";
				obj.jq = createGroup(obj);
				break;
			case QUESTION_OBJECT:
				obj.hierarchy.parentID = obj.hierarchy.parentID + "_items";
				obj.jq = createQuestion(obj);
				break;
			case NOTE_OBJECT:
				obj.hierarchy.parentID = obj.hierarchy.parentID + "_items";
				obj.jq = createNote(obj);
				break;
			case ERROR_OBJECT:
				var question = getJQElement(obj.hierarchy.parentID);
				if (!question.hasClass("error")) {
					// Add error class to primary question div.
					question.addClass("error");
				}
				obj.hierarchy.parentID = obj.hierarchy.parentID + "_errors";
				obj.jq = createError(obj);
				break;
			case ACTION_OBJECT:
				obj.hierarchy.parentID = obj.hierarchy.parentID + "_controls";
				obj.jq = createControl(obj);
				break;
			}

			// Do application-specific post processing, if any.
			if (window.postProcessCreate) {
				postProcessCreate(obj);
			}
		}
		else if (!handleError(ERROR_TYPES.NO_PARENT, [ obj.id ], "dynamicUI_main.processCreateList",
					objectToString(obj))) {
			return;
		}			
	}
}

/**
 * Update all HTML elements in the update list.
 * 
 * @param theList Array of fact objects.
 */
function processUpdateList(theList) {
	for (var i = 0; i < theList.length; i++) {
		var obj = theList[i];
		debugObject("processUpdateList() obj=", obj);

		obj.jq = getJQElement(obj.id);
		
		// Do application-specific pre processing, if any.
		if (window.preProcessUpdate) {
			preProcessUpdate(obj);
		}

		switch (obj.objType) {
		case QUESTIONNAIRE_OBJECT:
		case GROUP_OBJECT:
			updateGroup(obj);
			break;
		case QUESTION_OBJECT:
			updateQuestion(obj);
			break;
		case NOTE_OBJECT:
			updateNote(obj);
			break;
		//case ERROR_OBJECT:
		//	updateError(obj);
		//	break;
		case ACTION_OBJECT:
			updateControl(obj);
			break;
		}

		// Do application-specific post processing, if any.
		if (window.postProcessUpdate) {
			postProcessUpdate(obj);
		}
	}
}

/**
 * Called whenever the answer to a question is changed.
 * 
 * @param questionID String ID of the changed question.
 * @param newValue String The new answer.
 * @param tabForward Boolean (true = tabbed forward, false = tabbed backward, null = didn't tab out). 
 */
function handleChangeEvent(questionID, newValue, tabForward) {
	debug("handleChangeEvent() questionID=" + questionID + " newValue=" + newValue + " tabForward=" + tabForward);
	guiBusy("change");
	
	// Do application-specific pre processing, if any.
	if (window.preChangeEvent) {
		preChangeEvent(questionID, newValue);
	}

	if (window.onChangeEvent && onChangeEvent(questionID, newValue)) {
		debug("handleChangeEvent() default handling overridden for question: " + questionID);
	}
	else {
		refreshScreen(setQuestionAnswer(questionID, newValue), questionID, tabForward);
	}

	// Do application-specific post processing, if any.
	if (window.postChangeEvent) {
		postChangeEvent(questionID, newValue);
	}

	guiReady();
}

/**
 * Called whenever a Control is activated.
 * 
 * @param actionID String Unique ID of the Action.
 * @param actionType String Type of Action, one of "setActive", "showError" or "completion.
 * @param action String Details of Action.
 */
function handleActionEvent(actionID, actionType, action) {
	debug("handleActionEvent() actionID=" + actionID + " actionType=" + actionType + " action=" + action);
	guiBusy("action");
	
	// Do application-specific pre processing, if any.
	if (window.preActionEvent) {
		preActionEvent(actionID, actionType, action);
	}

	if (window.onActionEvent && onActionEvent(actionID, actionType, action)) {
		debug("handleActionEvent() default handling overridden for action: " + actionID);
	}
	else {
		switch (actionType) {
		case "setActive":
			// Clear everything and start again.
			getJQElement(htmlRootID).empty();
			refreshScreen(setActiveItem(action), null, null);
			break;
		case "showError":			
			if (window.onShowError) {
				onShowError(actionID, action);
				break;
			}				
			showError(action);
		    break;
		case "completion":			
			if (window.onCompleteQuestionaire) {
				action = onCompleteQuestionaire(actionURL, action);
				if (!isnull(document.myform.action))
				break;
			}			
			document.myform.action = actionURL + action;			
			document.myform.submit();
			break;
		default:
			handleError(ERROR_TYPES.INTERNAL, [ "invalid actionType: " + actionType ],
					"dynamicUI_main.handleActionEvent", "actionID=" + actionID + " action=" + action);
		}
	}

	// Do application-specific post processing, if any.
	if (window.postActionEvent) {
		postActionEvent(actionID, actionType, action);
	}

	guiReady();
}

/**
 * Returns the focusable element before or after the specified question.
 * 
 * @param lastFocusID String ID of the question which had focus last.
 * @param tabForward Boolean (true = tabbed forward, false = tabbed backward, null = didn't tab out). 
 * @return the HTML element to receive focus wrapped in a JQuery object or null to leave the cursor where ever it is.
 */
function getNextFocusElement(lastFocusID, tabForward) {
	debug("getNextFocusElement() lastFocusID=" + lastFocusID + " tabForward=" + tabForward);
	var retVal = null;
	var questions = $(".Question,.Controls");
	// Find all the elements in order.
	var elements = questions.not(".readonly").not(".readonly-inherited").find(":input:visible,a:visible");
	
	if (lastFocusID == null) {
		// Not triggered by question change, set focus on first available element
		if (elements.length > 0) {
			retVal = elements.eq(0);
		}
	}
	else if (tabForward != null) {
		if (elements.length > 0) {
			debug("getNextFocusElement() elements=" + elements.length);
			// Find the index of the last focus element.
			for (var i = 0; i < elements.length; i++) {
				var name = elements.eq(i).attr("name");
				debug("getNextFocusElements() element.name=" + name);
				if (lastFocusID == name) {
					if (tabForward) {
						if (i == (elements.length - 1)) {
							retVal = elements.eq(0);
						}
						else {
							retVal = elements.eq(i + 1);
						}
					}
					else {
						// Tabbed backward.
						if (i == 0) {
							retVal = elements.eq(elements.length - 1);
						}
						else {
							retVal = elements.eq(i - 1);
						}
					}
					break;
				}
			}
		}
	}
	else {
		// If we didn't tab out of the previous question, leave the cursor where it is.
	}

	if (isNull(retVal) || (retVal.length == 0)) {
		retVal = null;
		debug("getNextFocusElement() retVal=null");
	}
	else {
		debug("getNextFocusElement() retVal.id=" + retVal.attr("id"));
	}
	return retVal;
}

/**
 * Called when the GUI is busy.
 * 
 * @param event String What it is busy doing.
 */
function guiBusy(event) {
	if (isGUIBusyID != null) {
		var input = getJQElement(isGUIBusyID);
		if (input.length > 0) {
			debug("guiBusy() isGUIBusy=" + input.val());
			input.val("true");
		}
	}
	$("body").css("cursor", "wait"); 
    //$.blockUI({ message: "", css: { cursor: "wait" }, showOverlay: false }); 
}

/**
 * Called when the GUI is no longer busy.
 */
function guiReady() {
	$("body").css("cursor", "default"); 
	if (isGUIBusyID != null) {
		var input = getJQElement(isGUIBusyID);
		if (input.length > 0) {
			debug("guiReady() isGUIBusy=" + input.val());
			input.val("false");
		}
	}
	//$.unblockUI();
}
