package com.example.demo;

import com.example.jira.Events;
import com.example.jira.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StatesFirstApplicationTest {

  @Autowired private StateMachine<States, Events> stateMachine;

  @Nested
  @DisplayName("when initializing state machine")
  class WhenInitializingStateMachine {

    @BeforeEach
    void setUp() {
      StatesFirstApplicationTest.this.stateMachine.start();
    }

    @Test
    void initTest() {
      assertThat(StatesFirstApplicationTest.this.stateMachine.getState().getId()).isEqualTo(States.FREE);

      assertThat(StatesFirstApplicationTest.this.stateMachine).isNotNull();
    }

    @Test
    void testGreenFlow() {
      // Arrange
      // Act
      StatesFirstApplicationTest.this.stateMachine.sendEvent(Events.HANGUP);
      // Asserts
      assertThat(StatesFirstApplicationTest.this.stateMachine.getState().getId()).isEqualTo(States.BUSY);
    }

    @Test
    void testWrongWay() {
      // Arrange
      // Act
      StatesFirstApplicationTest.this.stateMachine.sendEvent(Events.HANGOFF);
      StatesFirstApplicationTest.this.stateMachine.sendEvent(Events.HANGUP);
      // Asserts
      assertThat(StatesFirstApplicationTest.this.stateMachine.getState().getId()).isEqualTo(States.BUSY);
    }
  }
}
