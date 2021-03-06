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
package org.tohu.examples.xml;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.tohu.server.ExecutionServerHelper;
import org.tohu.util.KnowledgeSessionHelper;

import com.thoughtworks.xstream.XStream;

/**
 * @author Damon Horrell
 */
public class OutputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StatefulKnowledgeSession knowledgeSession = new ExecutionServerHelper(request.getSession()).getKnowledgeSession();
		Map<String, Object> answers = new KnowledgeSessionHelper(knowledgeSession).getAnswers();
		XStream xs = new XStream();
		xs.alias("data", java.util.TreeMap.class);
		xs.registerConverter(new MapEntryConverter());
		String xml = xs.toXML(answers);
		request.getSession().setAttribute("xml", xml.replaceAll("&", "&amp;"));
		response.sendRedirect("../index.jsp");
	}

}
