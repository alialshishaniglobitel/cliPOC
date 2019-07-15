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
package org.mobicents.commons.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.mobicents.commons.annotations.ThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class RevolvingCounter {
  private final long initialValue;
  private final long limit;
  private AtomicLong count;
  private final Lock lock;
  
  public RevolvingCounter(final long limit) {
    this(0, limit);
  }
  
  public RevolvingCounter(final long initialValue, final long limit) {
    super();
    this.initialValue = initialValue;
    this.limit = limit;
    this.count = new AtomicLong();
    this.count.set(initialValue);
    this.lock = new ReentrantLock();
  }
  
  public long getAndIncrement() {
    long result = count.getAndIncrement();
    if(result >= limit) {
      while(!lock.tryLock()) { /* Spin */ }
      try {
        if(count.get() >= limit){
          result = initialValue;
          count.set(initialValue + 1);
        } else {
          result = getAndIncrement();
        }
      }
      finally { lock.unlock(); }
    }
    return result;
  }
  
  public long getCount() {
    return count.get();
  }
}
