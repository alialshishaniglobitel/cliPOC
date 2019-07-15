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

import org.mobicents.commons.annotations.NotThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@NotThreadSafe public class State {
  private final Action actionOnEnter;
  private final Action actionOnExit;
  private final String id;
  
  public State(final String id, final Action actionOnEnter, final Action actionOnExit) {
    super();
    checkNotNull(id);
    this.actionOnEnter = actionOnEnter;
    this.actionOnExit = actionOnExit;
    this.id = id;
  }
  
  private void checkNotNull(final String id) throws NullPointerException {
    if(id == null) {
      final StringBuilder buffer = new StringBuilder();
      buffer.append("A state can not be built with a null value for the id parameter.");
      throw new NullPointerException(buffer.toString());
    }
  }
  
  @Override public boolean equals(final Object object) {
    if(object == null) {
	  return false;
    } else if(this == object) {
	  return true;
	} else if (getClass() != object.getClass()) {
	  return false;
	}
	final State state = (State)object;
	if(!id.equals(state.getId())) { return false; }
	return true;
  }

  public Action getActionOnEnter() {
    return actionOnEnter;
  }
  
  public Action getActionOnExit() {
    return actionOnExit;
  }
  
  public String getId() {
    return id;
  }
  
  @Override public int hashCode() {
  	final int prime = 5;
  	int result = 1;
  	result = prime * result + id.hashCode();
  	return result;
  }
}
