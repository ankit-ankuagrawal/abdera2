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
package org.apache.abdera2.ext.geo;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.geo.Box;
import org.apache.abdera2.common.geo.Coordinate;
import org.apache.abdera2.common.geo.Line;
import org.apache.abdera2.common.geo.Multiple;
import org.apache.abdera2.common.geo.Point;
import org.apache.abdera2.common.geo.Polygon;
import org.apache.abdera2.common.geo.Position;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.ExtensibleElement;

/**
 * Basic support for the GeoRSS extensions to Atom: http://georss.org/1
 */
public class GeoHelper {

    public static final String W3C_GEO_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String SIMPLE_GEO_NS = "http://www.georss.org/georss";
    public static final String GML_NS = "http://www.opengis.net/gml";

    public static final QName QNAME_W3C_POINT = new QName(W3C_GEO_NS, "Point", "geo");
    public static final QName QNAME_W3C_LAT = new QName(W3C_GEO_NS, "lat", "geo");
    public static final QName QNAME_W3C_LONG = new QName(W3C_GEO_NS, "long", "geo");

    public static final QName QNAME_SIMPLE_POINT = new QName(SIMPLE_GEO_NS, "point", "georss");
    public static final QName QNAME_SIMPLE_LINE = new QName(SIMPLE_GEO_NS, "line", "georss");
    public static final QName QNAME_SIMPLE_POLYGON = new QName(SIMPLE_GEO_NS, "polygon", "georss");
    public static final QName QNAME_SIMPLE_BOX = new QName(SIMPLE_GEO_NS, "box", "georss");
    public static final QName QNAME_WHERE = new QName(SIMPLE_GEO_NS, "where", "georss");

    public static final QName QNAME_GML_POINT = new QName(GML_NS, "Point", "gml");
    public static final QName QNAME_GML_POS = new QName(GML_NS, "pos", "gml");
    public static final QName QNAME_GML_LINESTRING = new QName(GML_NS, "LineString", "gml");
    public static final QName QNAME_GML_POSLIST = new QName(GML_NS, "posList", "gml");
    public static final QName QNAME_GML_POLYGON = new QName(GML_NS, "Polygon", "gml");
    public static final QName QNAME_GML_EXTERIOR = new QName(GML_NS, "exterior", "gml");
    public static final QName QNAME_GML_LINEARRING = new QName(GML_NS, "LinearRing", "gml");
    public static final QName QNAME_GML_ENVELOPE = new QName(GML_NS, "Envelope", "gml");
    public static final QName QNAME_GML_LOWERCORNER = new QName(GML_NS, "lowerCorner", "gml");
    public static final QName QNAME_GML_UPPERCORNER = new QName(GML_NS, "upperCorner", "gml");

    public enum Encoding {
        SIMPLE, 
        W3C, 
        GML;
        
        public void addPosition(ExtensibleElement element, Position position) {
          switch (this) {
            case SIMPLE:
                addSimplePosition(element, position);
                break;
            case GML:
                addGmlPosition(element, position);
                break;
            case W3C:
                addW3CPosition(element, position);
                break;
          }         
        }
    }

    public static void addPosition(ExtensibleElement element, Position position) {
        Encoding.SIMPLE.addPosition(element, position);
    }

    private static void setPositionAttributes(Element pos, Position position) {
        if (pos != null) {
            if (position.getFeatureTypeTag() != null)
                pos.setAttributeValue("featuretypetag", position.getFeatureTypeTag());
            if (position.getRelationshipTag() != null)
                pos.setAttributeValue("relationshiptag", position.getRelationshipTag());
            if (position.getElevation() != null)
                pos.setAttributeValue("elev", position.getElevation().toString());
            if (position.getFloor() != null)
                pos.setAttributeValue("floor", position.getFloor().toString());
            if (position.getRadius() != null)
                pos.setAttributeValue("radius", position.getRadius().toString());
        }
    }

    public static String toString(Multiple multiple) {
      StringBuffer buf = new StringBuffer();
      for (Coordinate coord : multiple) {
          if (buf.length() > 0)
              buf.append(" ");
          buf.append(coord);
      }
      return buf.toString();
    }
    
    private static void addGmlPosition(ExtensibleElement element, Position position) {
        ExtensibleElement pos = element.addExtension(QNAME_WHERE);
        if (position instanceof Point) {
            Point point = (Point)position;
            ExtensibleElement p = pos.addExtension(QNAME_GML_POINT);
            p.addSimpleExtension(QNAME_GML_POS, point.getCoordinate().toString());
        } else if (position instanceof Line) {
            Multiple m = (Multiple)position;
            ExtensibleElement p = pos.addExtension(QNAME_GML_LINESTRING);
            p.addSimpleExtension(QNAME_GML_POSLIST, toString(m));
        } else if (position instanceof Polygon) {
            Multiple m = (Multiple)position;
            ExtensibleElement p = pos.addExtension(QNAME_GML_POLYGON);
            p = p.addExtension(QNAME_GML_EXTERIOR);
            p = p.addExtension(QNAME_GML_LINEARRING);
            p.addSimpleExtension(QNAME_GML_POSLIST, toString(m));
        } else if (position instanceof Box) {
            Box m = (Box)position;
            ExtensibleElement p = pos.addExtension(QNAME_GML_ENVELOPE);
            if (m.getLowerCorner() != null)
                p.addSimpleExtension(QNAME_GML_LOWERCORNER, m.getLowerCorner().toString());
            if (m.getUpperCorner() != null)
                p.addSimpleExtension(QNAME_GML_UPPERCORNER, m.getUpperCorner().toString());
        }
        setPositionAttributes(pos, position);
    }

    private static void addSimplePosition(ExtensibleElement element, Position position) {
        Element pos = null;
        if (position instanceof Point) {
            Point point = (Point)position;
            pos = element.addSimpleExtension(QNAME_SIMPLE_POINT, point.getCoordinate().toString());
        } else if (position instanceof Multiple) {
            Multiple line = (Multiple)position;
            QName qname =
                position instanceof Line ? QNAME_SIMPLE_LINE : position instanceof Box ? QNAME_SIMPLE_BOX
                    : position instanceof Polygon ? QNAME_SIMPLE_POLYGON : null;
            if (qname != null) {
                pos = element.addSimpleExtension(qname, toString(line));
            }
        }
        setPositionAttributes(pos, position);
    }

    private static void addW3CPosition(ExtensibleElement element, Position position) {
        if (!(position instanceof Point))
            throw new IllegalArgumentException("The W3C Encoding only supports Points");
        Element el = element.getExtension(QNAME_W3C_LAT);
        if (el != null)
            el.discard();
        el = element.getExtension(QNAME_W3C_LONG);
        if (el != null)
            el.discard();
        Point point = (Point)position;

        ExtensibleElement p = element.addExtension(QNAME_W3C_POINT);
        p.addSimpleExtension(QNAME_W3C_LAT, Double.toString(point.getCoordinate().getLatitude()));
        p.addSimpleExtension(QNAME_W3C_LONG, Double.toString(point.getCoordinate().getLongitude()));

    }

    private static Iterable<Position> _getPositions(ExtensibleElement element) {
        Set<Position> list = new LinkedHashSet<Position>();
        getW3CPosition(element, list);
        getSimplePosition(element, list);
        getGMLPosition(element, list);
        return list;
    }

    public static boolean isGeotagged(ExtensibleElement element) {
        if (element.getExtensions(QNAME_SIMPLE_POINT).size() > 0)
            return true;
        if (element.getExtensions(QNAME_SIMPLE_LINE).size() > 0)
            return true;
        if (element.getExtensions(QNAME_SIMPLE_BOX).size() > 0)
            return true;
        if (element.getExtensions(QNAME_SIMPLE_POLYGON).size() > 0)
            return true;
        if (element.getExtensions(QNAME_WHERE).size() > 0)
            return true;
        if (element.getExtensions(QNAME_W3C_POINT).size() > 0)
            return true;
        if (element.getExtensions(QNAME_W3C_LAT).size() > 0 && element.getExtensions(QNAME_W3C_LONG).size() > 0)
            return true;
        return false;
    }

    public static Iterator<Position> listPositions(ExtensibleElement element) {
        return _getPositions(element).iterator();
    }

    public static Position getAsPosition(Element element) {
        Position pos = null;
        QName qname = element.getQName();
        String text = element.getText();
        if (qname.equals(QNAME_GML_POINT)) {
            element = traverse((ExtensibleElement)element, QNAME_GML_POS);
            if (element != null && text != null) {
              pos = GeoHelper.<Point.Builder>getPositionAttributes(element,Point.make(text.trim())).get();
            }
        } else if (qname.equals(QNAME_GML_LINESTRING)) {
            element = traverse((ExtensibleElement)element, QNAME_GML_POSLIST);
            if (element != null && text != null) {
              pos = GeoHelper.<Line.Builder>getPositionAttributes(element,Line.make(Multiple.parse(text.trim()))).get();
            }
        } else if (qname.equals(QNAME_GML_POLYGON)) {
            element = traverse((ExtensibleElement)element, QNAME_GML_EXTERIOR, QNAME_GML_LINEARRING, QNAME_GML_POSLIST);
            if (element != null && text != null) {
                pos = GeoHelper.<Polygon.Builder>getPositionAttributes(element,Polygon.make(Multiple.parse(text.trim()))).get();
            }
        } else if (qname.equals(QNAME_GML_ENVELOPE)) {
            String lc = ((ExtensibleElement)element).getSimpleExtension(QNAME_GML_LOWERCORNER);
            String uc = ((ExtensibleElement)element).getSimpleExtension(QNAME_GML_UPPERCORNER);
            if (lc != null && uc != null) {
                Coordinate c1 = new Coordinate(lc);
                Coordinate c2 = new Coordinate(uc);
                pos = GeoHelper.<Box.Builder>getPositionAttributes(element,Box.make(c1,c2)).get();
            }
        } else if (qname.equals(QNAME_SIMPLE_POINT) && text != null) {
            pos = GeoHelper.<Point.Builder>getPositionAttributes(element,Point.make(text.trim())).get();
        } else if (qname.equals(QNAME_SIMPLE_LINE) && text != null) {
            pos = GeoHelper.<Line.Builder>getPositionAttributes(element,Line.make(Multiple.parse(text.trim()))).get();
        } else if (qname.equals(QNAME_SIMPLE_BOX) && text != null) {
            pos = GeoHelper.<Box.Builder>getPositionAttributes(element,Box.make(Multiple.parse(text.trim()))).get();
        } else if (qname.equals(QNAME_SIMPLE_POLYGON) && text != null) {
            pos = GeoHelper.<Polygon.Builder>getPositionAttributes(element,Polygon.make(Multiple.parse(text.trim()))).get();
        } else if (qname.equals(QNAME_W3C_POINT) || qname.equals(Constants.ENTRY)) {
            Set<Position> list = new LinkedHashSet<Position>();
            getW3CPosition((ExtensibleElement)element, list);
            for (Position p : list) {
              pos = p; 
              break;
            }
        }
        return pos;
    }

    public static Iterable<Position> getPositions(ExtensibleElement element) {
        return _getPositions(element);
    }

    private static void getSimplePosition(ExtensibleElement element, Set<Position> list) {
        Iterable<Element> elements = element.getExtensions(SIMPLE_GEO_NS);
        for (Element el : elements) {
            Position pos = getAsPosition(el);
            list.add(pos);
        }
    }

    private static void getGMLPosition(ExtensibleElement element, Set<Position> list) {
        Iterable<ExtensibleElement> elements = element.getExtensions(QNAME_WHERE);
        for (ExtensibleElement where : elements) {
            Position pos = null;
            List<ExtensibleElement> children = where.getElements();
            for (ExtensibleElement el : children) {
                pos = getAsPosition(el);
                list.add(pos);
            }
        }
    }

    private static <X extends Position.Builder<?>>X getPositionAttributes(Element pos, X builder) {
      if (builder != null) {
          String featuretypetag = pos.getAttributeValue("featuretypetag");
          String relationshiptag = pos.getAttributeValue("relationshiptag");
          String elevation = pos.getAttributeValue("elev");
          String floor = pos.getAttributeValue("floor");
          String radius = pos.getAttributeValue("radius");
          if (featuretypetag != null)
              builder.featureType(featuretypetag);
          if (featuretypetag != null)
            builder.relationship(relationshiptag);
          if (elevation != null)
            builder.elevation(Double.valueOf(elevation));
          if (floor != null)
            builder.floor(Double.valueOf(floor));
          if (radius != null)
            builder.radius(Double.valueOf(radius));
      }
      return builder;
  }
    
    private static ExtensibleElement traverse(ExtensibleElement element, QName... qnames) {
        for (QName qname : qnames) {
            element = element.getExtension(qname);
            if (element == null)
                break;
        }
        return element;
    }

    private static void getW3CPosition(ExtensibleElement element, Set<Position> list) {
        getSimpleW3CPosition(element, list);
        List<ExtensibleElement> points = element.getExtensions(QNAME_W3C_POINT);
        for (ExtensibleElement point : points)
            getSimpleW3CPosition(point, list);
    }

    private static void getSimpleW3CPosition(ExtensibleElement el, Set<Position> list) {
        String slat = el.getSimpleExtension(QNAME_W3C_LAT);
        String slong = el.getSimpleExtension(QNAME_W3C_LONG);
        if (slat != null && slong != null)
            list.add(Point.at(slat.trim() + " " + slong.trim()));
    }
}
