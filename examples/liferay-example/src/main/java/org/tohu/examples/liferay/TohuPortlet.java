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
package org.tohu.examples.liferay;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.drools.core.runtime.help.impl.XStreamXML;
import org.kie.api.command.Command;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.tohu.server.ExecutionServerHelper;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.thoughtworks.xstream.XStream;

/**
 * This is an example Tohu portlet based on the code in TohuExecutionServer.
 * 
 * TODO standardise this and move it into a new tohu-portlet module
 * 
 * @author Damon Horrell
 */
public class TohuPortlet extends com.liferay.util.bridges.mvc.MVCPortlet {

	private static final String AGENT = "agent";

	private static final String RESET = "RESET";

	private XStream xstream;

	public TohuPortlet() {
		xstream = XStreamXML.newXStreamMarshaller(new XStream());
	}

	/**
	 * @see com.liferay.util.bridges.mvc.MVCPortlet#serveResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse response) throws IOException, PortletException {
		HttpServletRequest request = ((LiferayPortletRequest) resourceRequest).getHttpServletRequest();
		HttpSession session = request.getSession();
		ExecutionServerHelper helper = new ExecutionServerHelper(session);
		String s = IOUtils.toString(request.getInputStream());
		if (RESET.equals(s)) {
			helper.removeKnowledgeSession();
		} else {
			StatefulKnowledgeSession knowledgeSession = helper.getKnowledgeSession();
			if (knowledgeSession == null) {
				String agentName = request.getParameter(AGENT);
				knowledgeSession = helper.newKnowledgeSession(agentName);
			}
			Command<?> command = (Command<?>) xstream.fromXML(s);
			Object results = knowledgeSession.execute(command);
			String xml = xstream.toXML(results);
			response.setContentType("text/xml");
			PrintWriter writer = response.getWriter();
			writer.write(xml);
			writer.close();
		}
	}

}
