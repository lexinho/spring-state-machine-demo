package com.example.jira;

import lombok.NonNull;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.trigger.Trigger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StateMachineResolverImpl<States, Events>
    implements StateMachineResolver<States, Events> {

  @Override
  public List<Events> getAvailableEvents(StateMachine<States, Events> stateMachine) {

    return stateMachine.getTransitions().stream()
        .filter(t -> this.isTransitionSourceFromCurrentState(t, stateMachine))
        .filter(t -> this.evaluateGuardCondition(stateMachine, t))
        .map(Transition::getTrigger)
        .map(Trigger::getEvent)
        .collect(Collectors.toList());
  }

  private boolean isTransitionSourceFromCurrentState(
      Transition<States, Events> transition, StateMachine<States, Events> stateMachine) {

    return stateMachine.getState().getId() == transition.getSource().getId();
  }

  private boolean evaluateGuardCondition(
      StateMachine<States, Events> stateMachine, Transition<States, Events> transition) {

    if (transition.getGuard() == null) {
      return true;
    }

    StateContext<States, Events> context = this.makeStateContext(stateMachine, transition);

    try {
      return transition.getGuard().evaluate(context);
    } catch (Exception e) {
      return false;
    }
  }

  @NonNull
  private DefaultStateContext<States, Events> makeStateContext(
      StateMachine<States, Events> stateMachine, Transition<States, Events> transition) {

    return new DefaultStateContext<>(
        StateContext.Stage.TRANSITION,
        null,
        null,
        stateMachine.getExtendedState(),
        transition,
        stateMachine,
        stateMachine.getState(),
        transition.getTarget(),
        null);
  }
}
