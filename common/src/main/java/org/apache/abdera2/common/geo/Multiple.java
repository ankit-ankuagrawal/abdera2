/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.common.geo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import static com.google.common.base.Preconditions.*;
@SuppressWarnings("unchecked")
public abstract class Multiple extends Position implements Iterable<Coordinate> {
  
  public static abstract class Builder<X extends Multiple> extends Position.Builder<X> {

    protected Collection<Coordinate> coordinates = 
      new ArrayList<Coordinate>();
    protected int maxpoints = -1;
    
    public <P extends Builder<X>>P noDuplicates() {
      this.coordinates = new LinkedHashSet<Coordinate>(coordinates);
      return (P)this;
    }
    
    public <P extends Builder<X>>P maximumCoordinates(int max) {
      checkArgument(max > -1, "Maximum must not be negative");
      this.maxpoints = max;
      checkState(coordinates.size() <= max, "Maximum coordinates exceeded", maxpoints);
      return (P)this;
    }
    
    public <P extends Builder<X>>P add(Coordinate coordinate) {
      checkState(maxpoints < 0 || coordinates.size()+1 <= maxpoints, "Maximum coordinates exceeded", maxpoints);
      this.coordinates.add(coordinate);
      return (P)this;
    }
    
    public <P extends Builder<X>>P add(double latitude, double longitude) {
      checkState(maxpoints < 0 || coordinates.size()+1 <= maxpoints, "Maximum coordinates exceeded", maxpoints);
      this.coordinates.add(Coordinate.at(latitude,longitude));
      return (P)this;
    }
    
    public <P extends Builder<X>>P add(String position) {
      checkState(maxpoints < 0 || coordinates.size()+1 <= maxpoints, "Maximum coordinates exceeded", maxpoints);
      this.coordinates.add(Coordinate.at(position));
      return (P)this;
    }
    
    public <P extends Builder<X>>P add(IsoPosition position) {
      checkState(maxpoints < 0 || coordinates.size()+1 <= maxpoints, "Maximum coordinates [%d] exceeded", maxpoints);
      this.coordinates.add(Coordinate.at(position));
      return (P)this;
    }
    
  }
  
  private static final long serialVersionUID = -401256381030284678L;
  protected final Collection<Coordinate> coordinates;

    protected Multiple(Builder<?> builder) {
      super(builder);
      this.coordinates = 
        builder.coordinates instanceof Set ?
          ImmutableSet.<Coordinate>copyOf(builder.coordinates) :
        ImmutableList.<Coordinate>copyOf(builder.coordinates);
    }

    public Iterator<Coordinate> iterator() {
        return coordinates.iterator();
    }
    
    public int size() {
      return coordinates.size();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((coordinates == null) ? 0 : coordinates.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Multiple other = (Multiple)obj;
        if (coordinates == null) {
            if (other.coordinates != null)
                return false;
        } else if (!coordinates.equals(other.coordinates))
            return false;
        return true;
    }

    public int compareTo(Position o) {
        throw new UnsupportedOperationException();
    }

    public static Iterable<Coordinate> parseIso(String value) {
      List<Coordinate> list = new ArrayList<Coordinate>();
      try {
          String[] points = value.trim().split("\\s+");
          for (int n = 0; n < points.length; n = n + 2) {
              IsoPosition pos = IsoPosition.parse(points[n]);
              list.add(new Coordinate(pos));
          }
          return list;
      } catch (Throwable t) {
          throw new RuntimeException("Error parsing coordinate pairs", t);
      }      
    }
    
    public static Iterable<Coordinate> parse(String value) {
      List<Coordinate> list = new ArrayList<Coordinate>();
        try {
            String[] points = value.trim().split("\\s+");
            for (int n = 0; n < points.length; n = n + 2) {
                double lat = Double.parseDouble(points[n]);
                double lon = Double.parseDouble(points[n + 1]);
                Coordinate c = new Coordinate(lat, lon);
                list.add(c);
            }
            return list;
        } catch (Throwable t) {
            throw new RuntimeException("Error parsing coordinate pairs", t);
        }
    }
}
