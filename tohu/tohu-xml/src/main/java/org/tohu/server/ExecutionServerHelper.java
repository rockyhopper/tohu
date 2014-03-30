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
package org.tohu.server;

import javax.servlet.http.HttpSession;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Manages a session-scoped KnowledgeSession.
 * 
 * @author Damon Horrell
 */
public class ExecutionServerHelper {

	private static final String KNOWLEDGE_SESSION = "knowledge.session";

	private static final String AGENT_CONFIG_DIRECTORY = "agent-config-directory";

	private HttpSession session;

	/**
	 * Constructs an ExecutionServerHelper for an HTTP session.
	 * 
	 * @param session
	 */
	public ExecutionServerHelper(HttpSession session) {
		this.session = session;
	}

	/**
	 * Returns the current knowledge session.
	 * 
	 * @return
	 */
	public StatefulKnowledgeSession getKnowledgeSession() {
		return (StatefulKnowledgeSession) session.getAttribute(KNOWLEDGE_SESSION);
	}

	/**
	 * Removes the knowledge session from the HTTP session.
	 * 
	 */
	public void removeKnowledgeSession() {
		StatefulKnowledgeSession knowledgeSession = getKnowledgeSession();
		if (knowledgeSession != null) {
			knowledgeSession.dispose();
			session.removeAttribute(KNOWLEDGE_SESSION);
		}
	}

	/**
	 * Creates a new knowledge session for the specified agent name.
	 * 
	 * @param agentName
	 * @return
	 */
	public StatefulKnowledgeSession newKnowledgeSession(String agentName) {
		removeKnowledgeSession();
		String agentFile = "/" + agentName + ".xml";
		String agentConfigDir = session.getServletContext().getInitParameter(AGENT_CONFIG_DIRECTORY);
		Resource resource;
		if (agentConfigDir.startsWith("classpath:")) {
			resource = ResourceFactory.newClassPathResource(agentConfigDir.replace("classpath:", "") + agentFile);
		} else {
			resource = ResourceFactory.newUrlResource(agentConfigDir + agentFile);
		}
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(resource, ResourceType.CHANGE_SET);
		if (knowledgeBuilder.hasErrors()) {
			throw new RuntimeException("Error in rules: " + knowledgeBuilder.getErrors());
		}
		StatefulKnowledgeSession knowledgeSession = knowledgeBuilder.newKnowledgeBase().newStatefulKnowledgeSession();
		session.setAttribute(KNOWLEDGE_SESSION, knowledgeSession);
		return knowledgeSession;
	}
}
