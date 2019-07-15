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
package org.mobicents.commons.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import org.junit.Test;

import org.mobicents.commons.annotations.ThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class EventBusTest {
  public EventBusTest() {
    super();
  }

  @Test public void test() throws InterruptedException {
    // Initialize the event bus.
	final ExecutorService executor = Executors.newCachedThreadPool();
	final EventBus.Builder builder = EventBus.builder();
	builder.setExecutor(executor);
	builder.setNumberOfThreads(4);
	builder.setQueueSize(100);
    final EventBus bus = builder.build();
    // Initialize the event handlers.
    final HelloEventHandler helloEventHandler = new HelloEventHandler();
    bus.register(helloEventHandler);
    final WorldEventHandler worldEventHandler = new WorldEventHandler();
    bus.register(worldEventHandler);
    final AllEventsHandler allEventsHandler = new AllEventsHandler();
    bus.register(allEventsHandler);
    // The finish barrier will let us know when we're finished.
    // In this case we expect to be finished after 200 events
    // have been handled.
    final FinishBarrier barrier = new FinishBarrier(200);
    bus.register(barrier);
    // Emit 50 events with a 100ms delay between events (x 4 threads).
    emit("hello", 50, 100, bus);
    emit("world", 50, 100, bus);
    emit("hello", 50, 100, bus);
    emit("world", 50, 100, bus);
    // Wait for all the events to get handled.
    barrier.await();
    // Verify that the events were properly handled.
    assertTrue(helloEventHandler.getNumberOfEventsHandled() == 100);
    assertTrue(worldEventHandler.getNumberOfEventsHandled() == 100);
    assertTrue(allEventsHandler.getNumberOfEventsHandled() == 200);
    // Clean up!
    executor.shutdownNow();
  }
  
  private void emit(final String name, final int times, final int timeout,
      final EventBus bus) {
    final Thread thread = new Thread(new Runnable() {
      @Override public synchronized void run() {
        Event<?> event = null;
        for(int counter = 0; counter < times; counter++) {
          if("hello".equals(name)) {
            event = new HelloEvent(this, System.currentTimeMillis(), "Hello");
          } else if("world".equals(name)) {
            event = new WorldEvent(this, System.currentTimeMillis(), "World");
          }
          try { bus.emit(event); wait(timeout); }
          catch(InterruptedException ignored) { }
        }
      }
    });
    thread.start();
  }
}
