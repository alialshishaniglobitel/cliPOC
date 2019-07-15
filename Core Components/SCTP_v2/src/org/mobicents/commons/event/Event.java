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

import org.mobicents.commons.annotations.NotThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@NotThreadSafe public class Event<T> {
  private final Object source;
  private final Long timestamp;
  private final T type;
  
  public Event(final Object source, final Long timestamp, final T type) {
    super();
    checkNotNull(source, timestamp, type);
    this.source = source;
    this.timestamp = timestamp;
    this.type = type;
  }
  
  private void checkNotNull(final Object source, final Long timestamp, final T type)
      throws NullPointerException {
    if(source == null) {
      throw new NullPointerException("An event may not have a null source.");
    } else if(timestamp == null) {
      throw new NullPointerException("An event may not have a null timestamp.");
    } else if(type == null) {
      throw new NullPointerException("An event may not have a null type.");
    }
  }

  @Override public boolean equals(final Object object) {
    if(object == null) {
      return false;
    } else if(this == object) {
      return true;
    } else if(getClass() != object.getClass()) {
      return false;
    }
	final Event<?> event = (Event<?>)object;
	if(!source.equals(event.getSource())) { return false; }
	if(!timestamp.equals(event.getTimestamp())) { return false; }
	if(!type.equals(event.getType())) { return false; }
	return true;
  }
  
  public Object getSource() {
    return source;
  }
  
  public Long getTimestamp() {
    return timestamp;
  }

  public T getType() {
    return type;
  }

  @Override public int hashCode() {
	final int prime = 5;
	int result = 1;
	result = prime * result + source.hashCode();
	result = prime * result + timestamp.hashCode();
	result = prime * result + type.hashCode();
	return result;
  }
}
