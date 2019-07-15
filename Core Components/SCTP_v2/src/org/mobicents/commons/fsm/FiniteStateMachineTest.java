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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import org.junit.Test;

import org.mobicents.commons.annotations.ThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class FiniteStateMachineTest {
  public FiniteStateMachineTest() {
    super();
  }
  
  @Test public void test() throws InterruptedException {
    final ExecutorService executor = Executors.newFixedThreadPool(4);
    final LightBulb bulb = new LightBulb();
    for(int counter = 0; counter < 4; counter++) {
      flipSwitch(executor, bulb, 5);
    }
    executor.shutdown();
    executor.awaitTermination(30, TimeUnit.SECONDS);
    assertTrue(bulb.getCount() == 20);
  }
  
  private void flipSwitch(final ExecutorService executor, final LightBulb bulb, final int times) {
    executor.execute(new Runnable() {
      @Override public void run() {
        for(int counter = 0; counter < times; counter++) {
          bulb.power();
        }
      }
    });
  }
}
