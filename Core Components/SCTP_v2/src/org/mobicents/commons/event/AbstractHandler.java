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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.mobicents.commons.annotations.NotThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@NotThreadSafe public abstract class AbstractHandler implements EventHandler {
  private final AtomicInteger counter;
  
  public AbstractHandler() {
    super();
    counter = new AtomicInteger(0);
  }

  @Override public abstract boolean accept(final Event<?> event);
  
  public int getNumberOfEventsHandled() {
    return counter.get();
  }

  @Override public void handle(final Event<?> event) {
    final int count = counter.incrementAndGet();
    final StringBuilder buffer = new StringBuilder();
    buffer.append(getClass().getName()).append(" ");
    buffer.append("Event #").append(count).append(" { ");
    buffer.append("Type=").append(event.getType()).append(", ");
    final Calendar calendar = new GregorianCalendar();
    final Date date = new Date(event.getTimestamp());
    calendar.setTime(date);
    final StringBuilder time = new StringBuilder();
    time.append(calendar.get(Calendar.HOUR_OF_DAY)).append(":");
    time.append(calendar.get(Calendar.MINUTE)).append(":");
    time.append(calendar.get(Calendar.SECOND));
    buffer.append("Time Of Occurence=").append(time.toString()).append(" ");
    buffer.append("}");
    System.out.println(buffer.toString());
  }
}
