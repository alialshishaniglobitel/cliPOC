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
package org.mobicents.commons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import org.junit.Test;

import org.mobicents.commons.annotations.NotThreadSafe;
import org.mobicents.commons.annotations.ThreadSafe;
import org.mobicents.commons.util.concurrent.RevolvingCounter;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class RevolvingCounterTest {
  public RevolvingCounterTest() {
    super();
  }
  
  private List<Consumer> getConsumers(final int count, 
      final RevolvingCounter counter) {
    final List<Consumer> consumers = new ArrayList<Consumer>();
    for(int index = 0; index < count; index++) {
      consumers.add(new Consumer(counter));
    }
    return consumers;
  }
  
  @Test public void test() {
    final RevolvingCounter counter = new RevolvingCounter(100);
    final List<Consumer> consumers = getConsumers(1000000, counter);
    final ExecutorService executor = Executors.newCachedThreadPool();
    try { executor.invokeAll(consumers); }
    catch(InterruptedException ignored) { }
    // Validate the results.
    final Map<Integer, Integer> scorecard = tally(consumers);
    for(final int score : scorecard.values()) {
      assertTrue(score == 10000);
    }
  }
  
  private Map<Integer, Integer> tally(final List<Consumer> consumers) {
    final Map<Integer, Integer> scorecard = new HashMap<Integer, Integer>();
    for(final Consumer consumer : consumers) {
      final int key = (int)consumer.getValue();
      if(!scorecard.containsKey(key)) {
        scorecard.put(key, 1);
      } else {
        final int value = scorecard.get(key);
        scorecard.put(key, value + 1);
      }
    }
    return scorecard;
  }
  
  @NotThreadSafe private final class Consumer implements Callable<Void> {
    private final RevolvingCounter counter;
    private long value;
    
    private Consumer(final RevolvingCounter counter) {
      super();
      this.counter = counter;
    }
    
    private long getValue() {
      return value;
    }

	@Override public Void call() {
      value = counter.getAndIncrement();
      return null;
	}
  }
}
