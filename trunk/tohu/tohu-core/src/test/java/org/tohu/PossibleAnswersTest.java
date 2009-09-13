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
import org.tohu.MultipleChoiceQuestion;
import org.tohu.MultipleChoiceQuestion.PossibleAnswer;

/**
 * @author Damon Horrell
 */
public class PossibleAnswersTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswers() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswers(new PossibleAnswer[] { new PossibleAnswer(null, "select..."),
				new PossibleAnswer("a", "apple"), new PossibleAnswer("b", "banana"), null,
				new PossibleAnswer("c", "carrot, cucumber, or cauliflower"), new PossibleAnswer("d"),
				new PossibleAnswer("e=?", "e=mc^2"), new PossibleAnswer("===", "=equals=") });
		assertArrayEquals(new PossibleAnswer[] { new PossibleAnswer(null, "select..."), new PossibleAnswer("a", "apple"),
				new PossibleAnswer("b", "banana"), new PossibleAnswer("c", "carrot, cucumber, or cauliflower"),
				new PossibleAnswer("d"), new PossibleAnswer("e=?", "e=mc^2"), new PossibleAnswer("===", "=equals=") }, question
				.getPossibleAnswers());
		assertEquals(
				"null=select...,a=apple,b=banana,c=carrot\\, cucumber\\, or cauliflower,d=,e\\=?=e\\=mc^2,\\=\\=\\==\\=equals\\=",
				question.getPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersAsString() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question
				.setPossibleAnswersAsString("null=select...,a=apple,b=banana,c=carrot\\, cucumber\\, or cauliflower,d=,e\\=?=e\\=mc^2,\\=\\=\\==\\=equals\\=");
		assertArrayEquals(new PossibleAnswer[] { new PossibleAnswer(null, "select..."), new PossibleAnswer("a", "apple"),
				new PossibleAnswer("b", "banana"), new PossibleAnswer("c", "carrot, cucumber, or cauliflower"),
				new PossibleAnswer("d"), new PossibleAnswer("e=?", "e=mc^2"), new PossibleAnswer("===", "=equals=") }, question
				.getPossibleAnswers());
		assertEquals(
				"null=select...,a=apple,b=banana,c=carrot\\, cucumber\\, or cauliflower,d=,e\\=?=e\\=mc^2,\\=\\=\\==\\=equals\\=",
				question.getPossibleAnswersAsString());
	}

	@Test
	public void testSetPossibleAnswersWithIdContainingComma() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		try {
			question.setPossibleAnswers(new PossibleAnswer[] { new PossibleAnswer("a", "apple"),
					new PossibleAnswer("b", "banana"), null, new PossibleAnswer("c,d", "carrot, cucumber, or donkey") });
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersNull() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswers(null);
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersAsStringNull() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswersAsString(null);
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersEmpty() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswers(new PossibleAnswer[0]);
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersAsStringEmpty() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswersAsString("");
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getPossibleAnswersAsString());
	}

}
