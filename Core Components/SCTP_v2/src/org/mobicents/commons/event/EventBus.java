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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.mobicents.commons.annotations.NotThreadSafe;
import org.mobicents.commons.annotations.ThreadSafe;

import com.globitel.utilities.commons.logger.MyLoggerFactory;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class EventBus {
  private final List<Dispatcher> dispatchers;
  private final AbstractSet<EventHandler> handlers;
  private final BlockingQueue<Event<?>> queue;
  
  private EventBus(final List<Dispatcher> dispatchers, final Executor executor,
      final AbstractSet<EventHandler> handlers, final BlockingQueue<Event<?>> queue) {
    super();
    this.dispatchers = dispatchers;
    this.handlers = handlers;
    this.queue = queue;
    for(final Dispatcher dispatcher : dispatchers) {
      executor.execute(dispatcher);
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public boolean contains(final EventHandler handler) {
    return handlers.contains(handler);
  }
  
  public void emit(final Event<?> event) throws InterruptedException {
    queue.put(event);
  }
  
  public void register(final EventHandler handler) {
    handlers.add(handler);
  }
  
  public void terminate() {
    for(final Dispatcher dispatcher : dispatchers) {
      dispatcher.stop();
    }
  }
  
  public void unregister(final EventHandler handler) {
    handlers.remove(handler);
  }
  
  @NotThreadSafe public static final class Builder {
    private Executor executor;
    private int numberOfThreads;
    private int queueSize;
    
    private Builder() {
      super();
      executor = null;
      numberOfThreads = 1;
      queueSize = 10;
    }
    
    public EventBus build() {
      checkNotNull(executor);
      final AbstractSet<EventHandler> handlers = new CopyOnWriteArraySet<EventHandler>();
      final BlockingQueue<Event<?>> queue = new ArrayBlockingQueue<Event<?>>(queueSize);
      final List<Dispatcher> dispatchers = new ArrayList<Dispatcher>();
      for(int counter = 0; counter < numberOfThreads; counter++) {
        dispatchers.add(new Dispatcher(handlers, queue));
      }
      return new EventBus(Collections.unmodifiableList(dispatchers), executor, handlers, queue);
    }
    
    private void checkNotNull(final Executor executor) throws NullPointerException {
  	  if(executor == null) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("An event bus can not be built with a null value for the executor.\n");
        buffer.append("Please set an executor before calling the build() method on ");
        buffer.append(getClass().getName());
        throw new NullPointerException(buffer.toString());
      }
    }
    
    public Builder setExecutor(final Executor executor) {
      this.executor = executor;
      return this;
    }
    
    public Builder setNumberOfThreads(final int numberOfThreads) {
      this.numberOfThreads = numberOfThreads;
      return this;
    }
    
    public Builder setQueueSize(final int queueSize) {
      this.queueSize = queueSize;
      return this;
    }
  }
  
  private static final class Dispatcher implements Runnable {
    private final AbstractSet<EventHandler> handlers;
    private final BlockingQueue<Event<?>> queue;
    
    private volatile boolean dispatching;
    
    private Dispatcher(final AbstractSet<EventHandler> handlers,
        final BlockingQueue<Event<?>> queue) {
      super();
      this.dispatching = true;
      this.handlers = handlers;
      this.queue = queue;
    }

	@Override public void run() {
      while(dispatching) {
        Event<?> event = null;
        try {event = queue.take(); }
        catch(InterruptedException ignored) { }
        if(event != null) {
          for(final EventHandler handler : handlers) {
            if(MyLoggerFactory.getInstance().getAPILogger().isTraceEnabled()) {
              final StringBuilder buffer = new StringBuilder();
              buffer.append("Processing a(n) event of type ");
              buffer.append(event.getType().toString()).append(".\n");
              buffer.append(event.toString());
              MyLoggerFactory.getInstance().getAPILogger().trace(buffer.toString());
            }
            try {
              if(handler.accept(event)) {
                handler.handle(event);
              }
            } catch(final Exception exception) {
              MyLoggerFactory.getInstance().getAPILogger().error(exception);
            }
          }
        }
      }
	}
	
	public synchronized void stop() {
	  if(dispatching) { dispatching = false; }
	}
  }
}
