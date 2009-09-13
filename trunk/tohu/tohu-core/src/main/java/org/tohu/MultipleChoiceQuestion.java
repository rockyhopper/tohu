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
 * An extension of <code>Question</code> which provides a list of possible answers. i.e. a multiple choice question.</code>
 * </p>
 * 
 * <p>
 * <code>presentationStyles</code> could be used to display the possible answers as e.g. radio buttons, or a drop down list.
 * </p>
 * 
 * @author Damon Horrell
 */
public class MultipleChoiceQuestion extends Question {

	private static final long serialVersionUID = 1L;

	/**
	 * Possible answers are represented internally as comma-delimited value/label pairs i.e. value1=label1,value2=label2,...
	 * 
	 * Any commas within the labels are escaped to \,
	 * 
	 * Any equals sign within either the values or labels are escaped to \=
	 */
	private String possibleAnswers;

	public MultipleChoiceQuestion() {
	}

	public MultipleChoiceQuestion(String id) {
		super(id);
	}

	public MultipleChoiceQuestion(String id, String label) {
		super(id, label);
	}

	/**
	 * Gets list of possible answers.
	 * 
	 * @return
	 */
	public PossibleAnswer[] getPossibleAnswers() {
		if (possibleAnswers == null) {
			return null;
		}
		List<PossibleAnswer> result = new ArrayList<PossibleAnswer>();
		String[] split = split(possibleAnswers, ",");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			String[] valueLabel = split(s, "=");
			String value = valueLabel[0];
			if (value.equals("null")) {
				value = null;
			}
			String label = valueLabel[1];
			if (label.equals("")) {
				label = null;
			}
			result.add(new PossibleAnswer(value, label));
		}
		return result.toArray(new PossibleAnswer[] {});
	}

	/**
	 * Sets list of possible answers.
	 * 
	 * @param possibleAnswers
	 */
	public void setPossibleAnswers(PossibleAnswer[] possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < possibleAnswers.length; i++) {
				if (possibleAnswers[i] != null) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					String value = possibleAnswers[i].value;
					if (value != null) {
						if (value.contains(",")) {
							throw new IllegalArgumentException();
						}
						value = value.replaceAll("=", "\\\\=");
					}
					sb.append(value);
					sb.append('=');
					if (possibleAnswers[i].label != null) {
						sb.append(possibleAnswers[i].label.replaceAll(",", "\\\\,").replaceAll("=", "\\\\="));
					}
				}
			}
			if (sb.length() > 0) {
				this.possibleAnswers = sb.toString();
			} else {
				this.possibleAnswers = null;
			}
		}
	}

	/**
	 * Sets list of possible answers.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * 
	 * <pre>
	 * question.setPossibleAnswers({
	 *   new PossibleAnswer(&quot;a&quot;, &quot;apple&quot;),
	 *   new PossibleAnswer(&quot;b&quot;, &quot;banana&quot;)
	 * });
	 * </pre>
	 * 
	 * @param possibleAnswers
	 */
	public void setPossibleAnswers(Object[] possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			setPossibleAnswers((PossibleAnswer[]) Arrays.asList(possibleAnswers).toArray(new PossibleAnswer[] {}));
		}
	}

	/**
	 * Gets list of possible answers as a comma delimited string.
	 * 
	 * TODO this method can be removed if Guvnor can support array of custom classes. Even just String[] would allow {"a=apple",
	 * "b=banana"} which is slightly better.
	 * 
	 * @return
	 * @deprecated
	 */
	public String getPossibleAnswersAsString() {
		return possibleAnswers;
	}

	/**
	 * Sets list of possible answers as a comma-delimited string.
	 * 
	 * TODO this method can be removed if Guvnor can support array of custom classes. Even just String[] would allow {"a=apple",
	 * "b=banana"} which is slightly better.
	 * 
	 * @param possibleAnswers
	 * @deprecated
	 */
	public void setPossibleAnswersAsString(String possibleAnswers) {
		if (possibleAnswers != null && possibleAnswers.equals("")) {
			possibleAnswers = null;
		}
		this.possibleAnswers = possibleAnswers;
	}

	/**
	 * Splits some text into words delimited by the specified delimiter.
	 * 
	 * Occurances of the delimiter d within the text are expected to be escaped as \d
	 * 
	 * @param string
	 * @param delimiter
	 * @return
	 */
	private String[] split(String text, String delimiter) {
		List<String> result = new ArrayList<String>();
		String[] split = text.split(delimiter, -1);
		for (int i = 0; i < split.length; i++) {
		}
		int i = 0;
		String s = "";
		while (i < split.length) {
			boolean continues = split[i].endsWith("\\");
			if (continues) {
				s += split[i].substring(0, split[i].length() - 1) + delimiter;
			} else {
				s += split[i];
				result.add(s);
				s = "";
			}
			i++;
		}
		return result.toArray(new String[] {});
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return super.toString() + " possibleAnswers=" + possibleAnswers;
	}

	public static class PossibleAnswer {

		private String value;

		private String label;

		public PossibleAnswer() {
		}

		public PossibleAnswer(String value) {
			this.value = value;
		}

		public PossibleAnswer(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final PossibleAnswer other = (PossibleAnswer) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return value + "=" + label;
		}

	}
}
