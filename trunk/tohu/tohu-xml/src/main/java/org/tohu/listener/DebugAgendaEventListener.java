package org.tohu.listener;

import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.Activation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugAgendaEventListener implements AgendaEventListener {

	private final static Logger logger = LoggerFactory.getLogger(DebugAgendaEventListener.class);

	/**
	 * @see org.drools.event.rule.AgendaEventListener#activationCreated(org.drools.event.rule.ActivationCreatedEvent)
	 */
	public void activationCreated(ActivationCreatedEvent activationcreatedevent) {
		Activation activation = activationcreatedevent.getActivation();
		String debugStr = "==>[ActivationCreated(" + activation.getPropagationContext().getPropagationNumber() + "): rule="
				+ activation.getRule().getName() + ";]";
		logger.debug(debugStr);
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#activationCancelled(org.drools.event.rule.ActivationCancelledEvent)
	 */
	public void activationCancelled(ActivationCancelledEvent activationcancelledevent) {
		Activation activation = activationcancelledevent.getActivation();
		String debugStr = "==>[activationCancelled(" + activation.getPropagationContext().getPropagationNumber() + "): rule="
				+ activation.getRule().getName() + ";]";
		logger.debug(debugStr);
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#beforeActivationFired(org.drools.event.rule.BeforeActivationFiredEvent)
	 */
	public void beforeActivationFired(BeforeActivationFiredEvent beforeactivationfiredevent) {
		Activation activation = beforeactivationfiredevent.getActivation();
		String debugStr = "==>[beforeActivationFired(" + activation.getPropagationContext().getPropagationNumber() + "): rule="
				+ activation.getRule().getName() + ";]";
		logger.debug(debugStr);
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#afterActivationFired(org.drools.event.rule.AfterActivationFiredEvent)
	 */
	public void afterActivationFired(AfterActivationFiredEvent afteractivationfiredevent) {
		Activation activation = afteractivationfiredevent.getActivation();
		String debugStr = "==>[afterActivationFired(" + activation.getPropagationContext().getPropagationNumber() + "): rule="
				+ activation.getRule().getName() + ";]";
		logger.debug(debugStr);
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#agendaGroupPopped(org.drools.event.rule.AgendaGroupPoppedEvent)
	 */
	public void agendaGroupPopped(AgendaGroupPoppedEvent agendagrouppoppedevent) {
		AgendaGroup agendaGroup = agendagrouppoppedevent.getAgendaGroup();
		String debugStr = "<==[AgendaGroupPopped(" + agendaGroup.getName() + "]";
		logger.debug(debugStr);
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#agendaGroupPushed(org.drools.event.rule.AgendaGroupPushedEvent)
	 */
	public void agendaGroupPushed(AgendaGroupPushedEvent agendagrouppushedevent) {
		AgendaGroup agendaGroup = agendagrouppushedevent.getAgendaGroup();
		String debugStr = "<==[agendaGroupPushed(" + agendaGroup.getName() + "]";
		logger.debug(debugStr);
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#afterRuleFlowGroupActivated(org.drools.event.rule.RuleFlowGroupActivatedEvent)
	 */
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0) {
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#afterRuleFlowGroupDeactivated(org.drools.event.rule.RuleFlowGroupDeactivatedEvent)
	 */
	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0) {
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#beforeRuleFlowGroupActivated(org.drools.event.rule.RuleFlowGroupActivatedEvent)
	 */
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0) {
	}

	/**
	 * @see org.drools.event.rule.AgendaEventListener#beforeRuleFlowGroupDeactivated(org.drools.event.rule.RuleFlowGroupDeactivatedEvent)
	 */
	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0) {
	}

}