/*
 * Copyright 2014 Damon Horrell
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
package org.tohu.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.drools.command.Command;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.help.impl.XStreamXML;

import com.thoughtworks.xstream.XStream;

/**
 * Servlet that provides an XML interface to the Drools knowledge session.
 * 
 * A separate knowledge session is maintained per HTTP session so that each user sees a different instance of the questionnaire.
 * 
 * This class provides equivalent functionality to drools-camel and was created because that module doesn't support a session-scoped
 * knowledge session. See https://issues.jboss.org/browse/DROOLS-424
 * 
 * TODO unit tests for this - check that the correct XML is returned including when changing pages and navigation branch (both of which
 * broke when upgrading to Drools 5.6)
 * 
 * @author Damon Horrell
 */
public class TohuExecutionServer extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String AGENT = "agent";

	private XStream xstream;

	public TohuExecutionServer() {
		xstream = XStreamXML.newXStreamMarshaller(new XStream());
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		ExecutionServerHelper helper = new ExecutionServerHelper(session);
		StatefulKnowledgeSession knowledgeSession = helper.getKnowledgeSession();
		if (knowledgeSession == null) {
			String agentName = request.getParameter(AGENT);
			knowledgeSession = helper.newKnowledgeSession(agentName);
		}
		Command<?> command = (Command<?>) xstream.fromXML(request.getInputStream());
		Object results = knowledgeSession.execute(command);
		String xml = xstream.toXML(results);
		response.setContentType("text/xml");
		PrintWriter writer = response.getWriter();
		writer.write(xml);
		writer.close();
	}

}
