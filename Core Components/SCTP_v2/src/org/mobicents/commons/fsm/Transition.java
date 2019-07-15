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

import org.mobicents.commons.annotations.Immutable;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@Immutable public final class Transition {
  private final Condition condition;
  private final State stateOnEnter;
  private final State stateOnExit;

  public Transition(final State stateOnEnter, final State stateOnExit,
      final Condition condition) {
    super();
    checkNotNull(stateOnEnter, stateOnExit);
    this.condition = condition;
    this.stateOnEnter = stateOnEnter;
    this.stateOnExit = stateOnExit;
  }
  
  private void checkNotNull(final State stateOnEnter, final State stateOnExit)
      throws NullPointerException {
    if(stateOnEnter == null || stateOnExit == null) {
      final StringBuilder buffer = new StringBuilder();
      if(stateOnEnter == null) {
        buffer.append("A transition can not be built with a null value for the state on enter.");
      } else if(stateOnExit == null) {
        buffer.append("A transition can not be built with a null value for the state on exit.");
      }
      buffer.append(getClass().getName());
      throw new NullPointerException(buffer.toString());
    }
  }
  
  @Override public boolean equals(final Object object) {
  	if(this == object) {
  	  return true;
  	} else if(object == null) {
  	  return false;
  	} else if(getClass() != object.getClass()) {
  	  return false;
  	}
  	final Transition transition = (Transition)object;
  	if(!stateOnEnter.equals(transition.getStateOnEnter())) { return false; }
  	if(!stateOnExit.equals(transition.getStateOnExit())) { return false; }
  	return true;
  }
  
  public Condition getCondition() {
    return condition;
  }
  
  public State getStateOnEnter() {
    return stateOnEnter;
  }
  
  public State getStateOnExit() {
    return stateOnExit;
  }
  
  @Override public int hashCode() { 
  	final int prime = 31;
  	int result = 1;
  	result = prime * result + stateOnEnter.hashCode();
  	result = prime * result + stateOnExit.hashCode();
  	return result;
  }
}
