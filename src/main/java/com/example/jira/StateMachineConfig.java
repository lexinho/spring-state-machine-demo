package com.example.jira;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.EnumSet;
import java.util.Optional;

@Log4j2
@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

  @Override
  public void configure(StateMachineConfigurationConfigurer<States, Events> config)
      throws Exception {
    config.withConfiguration().listener(this.listener()).autoStartup(true);
  }

  private StateMachineListener<States, Events> listener() {

    return new StateMachineListenerAdapter<>() {
      @Override
      public void eventNotAccepted(Message<Events> event) {
        log.error("Not accepted event: {}", event);
      }

      @Override
      public void transition(Transition<States, Events> transition) {
        log.warn(
            "MOVE from: {}, to: {}",
            this.ofNullableState(transition.getSource()),
            this.ofNullableState(transition.getTarget()));
      }

      private Object ofNullableState(State s) {
        return Optional.ofNullable(s).map(State::getId).orElse(null);
      }
    };
  }

  @Override
  public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
    states.withStates().initial(States.FREE).end(States.BUSY).states(EnumSet.allOf(States.class));
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(States.FREE)
        .target(States.BUSY)
        .event(Events.HANGUP)
        .and()
        .withExternal()
        .source(States.BUSY)
        .target(States.FREE)
        .event(Events.HANGOFF);
  }
}
