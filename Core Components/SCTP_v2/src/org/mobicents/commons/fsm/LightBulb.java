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

import java.util.HashSet;
import java.util.Set;

import org.mobicents.commons.annotations.ThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class LightBulb extends FiniteStateMachine {
  public final Power on;
  public final Power off;
  public int counter;
  
  public LightBulb() {
    super();
    on = new On(this);
    off = new Off(this);
    final Set<Transition> transitions = new HashSet<Transition>();
    transitions.add(new Transition(on, off, null));
    transitions.add(new Transition(off, on, null));
    initialize(off, transitions);
    counter = 0;
  }
  
  public int getCount() {
    return counter;
  }
  
  public void power() {
    lock();
    try {
      final Power power = (Power)getState();
      power.change();
    } finally {
      unlock();
    }
  }
  
  public void turnOff() throws LightBulbException {
    try { transition(null, off); counter++; }
    catch(final Exception exception) { throw new LightBulbException(exception); }
  }
  
  public void turnOn() throws LightBulbException {
    try { transition(null, on); counter++; }
    catch(final Exception exception) { throw new LightBulbException(exception); }
  }
}
