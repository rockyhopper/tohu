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
package org.tohu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Represents a general questionnaire containing a hierarchical arrangement of questions, and notes about how to complete the
 * questionnaire.
 * </p>
 * 
 * <p>
 * Note that the name <code>Questionnaire</code> is not intended to limit its use to specific cases, but rather it should be
 * considered a general-purpose model suitable for most types of form-based scenarios (which aren't usually referred to as a
 * questionnaire but are conceptually similar).
 * </p>
 * 
 * @author Damon Horrell
 */
public class Questionnaire extends Group {

	private static final long serialVersionUID = 1L;

	public static final String COMPLETION_ACTION_RETURN = "#return";

	private String activeItem;
	
	/**
	 * Items are represented internally as a comma-delimited string for efficient XML transport.
	 */
	private String masterListOfAllItems;



	private transient ArrayList<NavigationStackEntry> navigationStack = new ArrayList<NavigationStackEntry>();

	private String completionAction;

	private boolean invalidAnswers;

	public Questionnaire() {
		super.setActive(true);
	}

	public Questionnaire(String id) {
		super(id);
		super.setActive(true);
	}

	public Questionnaire(String id, String label) {
		super(id, label);
		super.setActive(true);
	}

	public String getActiveItem() {
		return activeItem;
	}
	
	/**
	 * Gets list of master item ids, regardless of whether they are conditional or not
	 * 
	 * @return
	 */
	public String[] getMasterListOfAllItems() {
		return masterListOfAllItems == null ? null : masterListOfAllItems.split(COMMA_SEPARATOR);
	}
	
	
	/**
	 * Sets list of master item ids. Values will NOT be trimmed.
	 * 
	 * @param masterListOfAllItems
	 */
	public void setMasterListOfAllItems(String[] masterListOfAllItems) {
		this.masterListOfAllItems = transformItemsArrayToString(masterListOfAllItems);
	}

	/**
	 * Sets list of master item ids.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * <p>
	 * <code>questionnaire.setMasterListOfAllItems({"a", "b"});</code>
	 * </p>
	 * 
	 * @param items
	 */
	public void setMasterListOfAllItems(Object[] items) {
		if (items == null) {
			this.masterListOfAllItems = null;
		} else {
			setMasterListOfAllItems((String[]) Arrays.asList(items).toArray(new String[] {}));
		}
	}


	/**
	 * Gets list of master item ids as a comma delimited string. Implemented for testing purpose only - package visibility
	 * 
	 * @return
	 */
	String getInternalMasterListOfAllItemsAsString() {
		return masterListOfAllItems;
	}
	
	protected void addItemWhenPositionalItemNotFound(String itemId, String otherItemId, boolean after) {
		if ((masterListOfAllItems == null) || (items == null)) {
			super.addItemWhenPositionalItemNotFound(itemId, otherItemId, after);
			return;
		}

		List<String> masterList = new ArrayList<String>(Arrays.asList(getMasterListOfAllItems()));
		int pos = masterList.indexOf(otherItemId);
		if (pos < 0) {
			super.addItemWhenPositionalItemNotFound(itemId, otherItemId, after);
			return;
		}
		
		List<String> itemList = new ArrayList<String>(Arrays.asList(getItems()));
		String theItemId = null;
		if (after) {
			for (int i = pos - 1; i >= 0; i--) {
				String id = masterList.get(i);
				int tempPos = itemList.indexOf(id);
				if (tempPos >= 0) {
					theItemId = id;
					break;
				}
			}
		}
		else {
			for (int i = pos; i < masterList.size(); i++) {
				String id = masterList.get(i);
				int tempPos = itemList.indexOf(id);
				if (tempPos >= 0) {
					theItemId = id;
					break;
				}
			}
		}

		if (theItemId == null) {
			super.addItemWhenPositionalItemNotFound(itemId, otherItemId, after);
			return;
		}

		if (after) {
			appendItem(itemId, theItemId);
		}
		else {
			insertItem(itemId, theItemId);
		}
	}



	/**
	 * Sets the active child <code>Item</code> within the questionnaire.
	 * 
	 * A typical scenerio is to place a number of <code>Group</code>s within the questionnaire and use them to represent the
	 * pages/screens of your application.
	 * 
	 * If <code>activeItem</code> is null then all items are considered to be active.
	 * 
	 * @param activeItem
	 */
	public void setActiveItem(String activeItem) {
		this.activeItem = activeItem;
	}

	/**
	 * @see org.tohu.Item#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		if (!active) {
			throw new UnsupportedOperationException("Questionnaire cannot be deactivated.");
		}
	}

	/**
	 * Sets the action to be performed when the questionnaire is complete.
	 * 
	 * The meaning of the action may vary from one UI implementation to another but it could be used for a URL to redirect to or
	 * the name of a method to invoke.
	 * 
	 * @return
	 */
	public String getCompletionAction() {
		return completionAction;
	}

	public void setCompletionAction(String completionAction) {
		this.completionAction = completionAction;
	}

	public boolean isInvalidAnswers() {
		return invalidAnswers;
	}

	/**
	 * This is invoked by the Pixie Dust. Do not call it directly.
	 * 
	 * @param invalidAnswers
	 */
	public void setInvalidAnswers(boolean invalidAnswers) {
		this.invalidAnswers = invalidAnswers;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return super.toString() + " activeItem=" + getActiveItem() + " completionAction=" + getCompletionAction() + " masterListOfAllItems=" + masterListOfAllItems
				+ " pushedActiveItems=" + navigationStack + " invalidAnswers=" + invalidAnswers;
	}

	/**
	 * This is used to push a new navigation path onto the questionnaire. On completion, the user will be returned to the previous
	 * path.
	 * 
	 * @param newItems
	 * @param newActiveItem
	 * @return
	 */
	public void navigationBranch(String[] newItems, String newActiveItem) {
		navigationBranch(newItems, newActiveItem, COMPLETION_ACTION_RETURN);
	}

	/**
	 * This is used to push a new navigation path onto the questionnaire with a new completionAction.
	 * 
	 * @param newItems
	 * @param newActiveItem
	 * @param newCompletionAction
	 * 
	 * @return
	 */
	public void navigationBranch(String[] newItems, String newActiveItem, String newCompletionAction) {
		if ((newItems == null) || (newItems.length == 0)) {
			throw new IllegalArgumentException("There are no valid new items to push onto stack.");
		}
		NavigationStackEntry entry = new NavigationStackEntry(this);
		navigationStack.add(0, entry);
		this.setItems(newItems);
		this.activeItem = newActiveItem;
		this.completionAction = newCompletionAction;
	}

	/**
	 * This is used to pop the new navigation path off the questionnaire, reverting to the previous one.
	 * 
	 * @return
	 */
	public void navigationReturn() {
		if (navigationStack.size() == 0) {
			throw new IllegalStateException();
		}
		NavigationStackEntry entry = navigationStack.remove(0);
		this.activeItem = entry.activeItem;
		this.items = entry.items;
		this.masterListOfAllItems = entry.masterListOfAllItems;
		this.setCompletionAction(entry.completionAction);
	}

	/**
	 * Returns true if the questionnaire is in a navigation branch, or false if it is in the main flow.
	 * 
	 * @return
	 */
	public boolean isBranched() {
		return !navigationStack.isEmpty();
	}

	private class NavigationStackEntry {

		private String items;
		private String activeItem;
		private String completionAction;
		private String masterListOfAllItems;

		private NavigationStackEntry(Questionnaire questionnaire) {
			this.items = questionnaire.getInternalItemsAsString();
			this.activeItem = questionnaire.getActiveItem();
			this.completionAction = questionnaire.getCompletionAction();
			this.masterListOfAllItems = questionnaire.masterListOfAllItems;
		}

	}
	
	/**
	 * Removes itemId from the existing list, but only if it is NOT the last item (page)
	 * 
	 * @param itemId
	 *            The value to remove. Ignore if null or doesn't exist
	 * @return 
	 * 			  The index of the removed item, or -1 if not found
	 */
	public int removeItem(String itemId) {
		int pos = super.removeItem(itemId);
		if ((pos > -1) && (itemId.equals(getActiveItem()))) {
			if (items == null) {
				System.out.println("Warning: removal of last page in a questionnaire is NOT being actioned");
				addItem(itemId);
			}
			else {
				List<String> items = new ArrayList<String>(Arrays.asList(getItems()));
				if (pos == 0) {
					setActiveItem(items.get(0));
				}
				else {
					// The -1 handles when removed the last item
					setActiveItem(items.get(pos - 1));
				}
			}
		}
		return pos;
	}
	
}
