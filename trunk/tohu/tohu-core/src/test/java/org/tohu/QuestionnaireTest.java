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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.tohu.Questionnaire;

/**
 * @author Derek Rendall
 */
public class QuestionnaireTest {

	@Test
	public void testBranch() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		q.navigationBranch(new String[] { "x","y","z" }, "y");
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationBranch(new String[] { "1","2","3" }, "3");
		assertArrayEquals(new String[] { "1","2","3" }, q.getItems());
		assertEquals("3", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "a","b","c" }, q.getItems());
		assertEquals("a", q.getActiveItem());
		assertEquals("default", q.getCompletionAction());
	}

	@Test
	public void testBranchWithAction() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		q.navigationBranch(new String[] { "x","y","z" }, "y", "action1");
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals("action1", q.getCompletionAction());
		q.navigationBranch(new String[] { "1","2","3" }, "3", "action2");
		assertArrayEquals(new String[] { "1","2","3" }, q.getItems());
		assertEquals("3", q.getActiveItem());
		assertEquals("action2", q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals("action1", q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "a","b","c" }, q.getItems());
		assertEquals("a", q.getActiveItem());
		assertEquals("default", q.getCompletionAction());
	}

	@Test
	public void testBranchInvalidEmpty() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		try {
			q.navigationBranch(null, "y", "action1");
			fail("no items on new branch");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testReturnToFar() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		q.navigationBranch(new String[] { "x","y","z" }, "y");
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationBranch(new String[] { "1","2","3" }, "3");
		assertArrayEquals(new String[] { "1","2","3" }, q.getItems());
		assertEquals("3", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "a","b","c" }, q.getItems());
		assertEquals("a", q.getActiveItem());
		assertEquals("default", q.getCompletionAction());
		try {
			q.navigationReturn();
			fail("no more items to pop");
		} catch (IllegalStateException e) {
			// expected
		}
	}
	
	@Test
	public void testItemHandlingForQuestionnaire() {
		Questionnaire q = new Questionnaire();
		String[] itemList = new String[] { "a" };
		q.setItems(itemList);
		q.setActiveItem("a");
		q.setCompletionAction("default");
		
		
		// test insert/append without master list - just to check the group based tests are still
		// ok for the questionnaire
		q.appendItem("k", "a");
		assertEquals("a,k", q.getInternalItemsAsString());
		q.insertItem("e", "k");
		assertEquals("a,e,k", q.getInternalItemsAsString());
		
		// assign decent itemList for use later on
		itemList = q.getItems();
		assertArrayEquals(new String[] { "a","e","k" }, itemList);
		
		
		String[] masterItemList = new String[] { "a", "b", "c", "d", "e", "f", "j", "h", "i" };
		q.setMasterListOfAllItems(masterItemList);
		
		// Test use of an item that appears in items but not master - should never be the case
		// if do things properly. Remember that the Group test handles
		q.insertItem("j", "k");
		assertEquals("a,e,j,k", q.getInternalItemsAsString());
		q.appendItem("l", "k");
		assertEquals("a,e,j,k,l", q.getInternalItemsAsString());
		
		// test adding when referred to item does not exist
		q.insertItem("m", "o");
		assertEquals("a,e,j,k,l,m", q.getInternalItemsAsString());
		q.appendItem("n", "o");
		assertEquals("a,e,j,k,l,m,n", q.getInternalItemsAsString());
		
		// test removing, and impact on active item
		q.removeItem("n");
		assertEquals("a,e,j,k,l,m", q.getInternalItemsAsString());
		assertEquals("a", q.getActiveItem());
		q.removeItem("a");
		assertEquals("e,j,k,l,m", q.getInternalItemsAsString());
		assertEquals("e", q.getActiveItem());
		q.setActiveItem("l");
		q.removeItem("l");
		assertEquals("e,j,k,m", q.getInternalItemsAsString());
		assertEquals("k", q.getActiveItem());
		q.setActiveItem("m");
		q.removeItem("m");
		assertEquals("e,j,k", q.getInternalItemsAsString());
		assertEquals("k", q.getActiveItem());
	}

	@Test
	public void testInsertingPagesBasedOnMasterItemList() {
		Questionnaire q = new Questionnaire();
		String[] itemList = new String[] { "a", "e", "k" };
		q.setItems(itemList);
		assertEquals("a,e,k", q.getInternalItemsAsString());
				
		String[] masterItemList = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k" };
		q.setMasterListOfAllItems(masterItemList);
		assertEquals("a,b,c,d,e,f,g,h,i,j,k", q.getInternalMasterListOfAllItemsAsString());
		
		// Append
		q.appendItem("d", "c");
		assertEquals("a,d,e,k", q.getInternalItemsAsString());
		q.appendItem("c", "b");
		assertEquals("a,c,d,e,k", q.getInternalItemsAsString());
		q.appendItem("b", "a");
		assertEquals("a,b,c,d,e,k", q.getInternalItemsAsString());
		
		// insert
		q.insertItem("h", "j");
		assertEquals("a,b,c,d,e,h,k", q.getInternalItemsAsString());
		q.insertItem("i", "k");
		assertEquals("a,b,c,d,e,h,i,k", q.getInternalItemsAsString());
		q.insertItem("f", "g");
		assertEquals("a,b,c,d,e,f,h,i,k", q.getInternalItemsAsString());
		q.insertItem("j", "k");
		assertEquals("a,b,c,d,e,f,h,i,j,k", q.getInternalItemsAsString());
		q.insertItem("g", "h");
		assertEquals("a,b,c,d,e,f,g,h,i,j,k", q.getInternalItemsAsString());
	}



}
