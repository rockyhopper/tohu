package org.tohu.listener;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugAgendaEventListener implements AgendaEventListener {

	private final static Logger logger = LoggerFactory.getLogger(DebugAgendaEventListener.class);

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#matchCreated(org.kie.api.event.rule.MatchCreatedEvent)
	 */
	public void matchCreated(MatchCreatedEvent event) {
		logger.debug("matchCreated " + event.getMatch().getRule().getName());
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#matchCancelled(org.kie.api.event.rule.MatchCancelledEvent)
	 */
	public void matchCancelled(MatchCancelledEvent event) {
		logger.debug("matchCancelled " + event.getMatch().getRule().getName());
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#beforeMatchFired(org.kie.api.event.rule.BeforeMatchFiredEvent)
	 */
	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		logger.debug("beforeMatchFired " + event.getMatch().getRule().getName());
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent)
	 */
	public void afterMatchFired(AfterMatchFiredEvent event) {
		logger.debug("afterMatchFired " + event.getMatch().getRule().getName());
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#agendaGroupPopped(org.kie.api.event.rule.AgendaGroupPoppedEvent)
	 */
	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#agendaGroupPushed(org.kie.api.event.rule.AgendaGroupPushedEvent)
	 */
	public void agendaGroupPushed(AgendaGroupPushedEvent event) {
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#beforeRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent)
	 */
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#afterRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent)
	 */
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#beforeRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent)
	 */
	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
	}

	/**
	 * @see org.kie.api.event.rule.AgendaEventListener#afterRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent)
	 */
	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
	}

}