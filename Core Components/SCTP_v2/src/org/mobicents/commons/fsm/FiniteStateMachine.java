/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.commons.fsm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.mobicents.commons.annotations.NotThreadSafe;
import org.mobicents.commons.event.Event;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@NotThreadSafe public abstract class FiniteStateMachine {
  private final Lock lock;
  private State state;
  private Map<State, Map<State, Transition>> transitions;
  
  protected FiniteStateMachine() {
    super();
    this.lock = new ReentrantLock();
  }
  
  private boolean canTransitionTo(final State state) {
    return transitions.get(this.state).containsKey(state);
  }
  
  private void checkIsInitialized() throws FiniteStateMachineException {
    if(state == null || transitions == null) {
      final StringBuilder buffer = new StringBuilder();
      buffer.append("The finite state machine has not been initialized. ");
      buffer.append("Please initialize the state machine before using it.");
      throw new FiniteStateMachineException(buffer.toString());
    }
  }
  
  private void checkNotNull(final State state) throws NullPointerException {
    if(state == null) {
      final StringBuilder buffer = new StringBuilder();
      buffer.append("The state parameter can not be null. ");
      buffer.append("Please provide a usable state and try again.");
      throw new NullPointerException(buffer.toString());
    }
  }
  
  private void checkNotNull(final State state, final Set<Transition> transitions)
      throws NullPointerException {
    checkNotNull(state);
    if(transitions == null) {
      final StringBuilder buffer = new StringBuilder();
      buffer.append("The transitions parameter can not be null. ");
      buffer.append("Please provide a set of transitions and try again.");
      throw new NullPointerException(buffer.toString());
    }
  }
  
  private String conditionFailed(final Event<?> event, final Transition transition) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("The condition guarding a transition from a(n) ");
    buffer.append(transition.getStateOnEnter().getId());
    buffer.append(" state to a(n) ").append(transition.getStateOnExit().getId());
    buffer.append(" state has failed. The event type that caused the failure is of type ");
    buffer.append(event.getType().getClass().getName());
    return buffer.toString();
  }
  
  private String finiteStateMachineFailed() {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("This finite state machine failed. It may be in an illegal or unusable state.");
    buffer.append("Please re-initialize before continuing use of this finite state machine.");
    return buffer.toString();
  }
  
  protected State getState() {
    lock();
    try {
      return state;
    } finally {
      unlock();
    }
  }
  
  private Transition getTransitionTo(final State state) {
    return transitions.get(this.state).get(state);
  }
  
  protected void initialize(final State state, final Set<Transition> transitions) {
    checkNotNull(state, transitions);
    lock();
    try {
      this.state = state;
      this.transitions = toImmutableMap(transitions);
    } finally {
      unlock();
    }
  }
  
  protected void lock() {
    lock.lock();
  }
  
  private String noTransitionFound(final State from, final State to) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("No transition could be found from a(n) ");
    buffer.append(from.getId()).append(" state to a(n) ");
    buffer.append(to.getId()).append(" state.");
    return buffer.toString();
  }
  
  protected <T> void transition(final Event<T> event, final State state)
      throws FiniteStateMachineException, NullPointerException,
      TransitionFailedException, TransitionNotFoundException {
    checkNotNull(state);
    checkIsInitialized();
    lock();
    try {
      if(!canTransitionTo(state)) {
        final String message = noTransitionFound(this.state, state);
        throw new TransitionNotFoundException(message, event, state);
      }
      final Transition transition = getTransitionTo(state);
      final Condition condition = transition.getCondition();
      boolean accept = true;
      if(condition != null) {
        accept = condition.accept(event, transition);
      }
      if(accept) {
        final Action actionOnExit = this.state.getActionOnExit();
        if(actionOnExit != null) {
          actionOnExit.execute(event, this.state);
        }
        this.state = state;
        final Action actionOnEnter = this.state.getActionOnEnter();
        if(actionOnEnter != null) {
          actionOnEnter.execute(event, this.state);
        }
      } else {
        final String message = conditionFailed(event, transition);
        throw new TransitionFailedException(message, event, transition);
      }
    } catch(final RuntimeException exception) {
      final String message = finiteStateMachineFailed();
      throw new FiniteStateMachineException(message, exception);
    } finally {
      unlock();
    }
  }
  
  private Map<State, Map<State, Transition>> toImmutableMap(final Set<Transition> transitions) {
    final Map<State, Map<State, Transition>> map = new HashMap<State, Map<State, Transition>>();
    for(final Transition transition : transitions) {
      final State stateOnEnter = transition.getStateOnEnter();
      if(!map.containsKey(stateOnEnter)) {
        map.put(stateOnEnter, new HashMap<State, Transition>());
      }
      final State stateOnExit = transition.getStateOnExit();
      map.get(stateOnEnter).put(stateOnExit, transition);
    }
    return Collections.unmodifiableMap(map);
  }
  
  protected boolean tryLock() {
    return lock.tryLock();
  }
  
  protected void unlock() {
    lock.unlock();
  }
}
