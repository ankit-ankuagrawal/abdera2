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
package org.apache.abdera2.parser.axiom;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Div;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMDiv extends FOMExtensibleElement implements Div {

    private static final long serialVersionUID = -2319449893405850433L;

    public FOMDiv() {
        super(Constants.DIV);
    }

    public FOMDiv(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMDiv(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    public FOMDiv(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(qname, parent, factory, builder);
    }

    public String[] getXhtmlClass() {
        String _class = getAttributeValue(CLASS);
        String[] classes = null;
        if (_class != null) {
            classes = _class.split(" ");
        }
        return classes;
    }

    public String getId() {
        return getAttributeValue(AID);
    }

    public String getTitle() {
        return getAttributeValue(ATITLE);
    }

    public Div setId(String id) {
      complete();
      return setAttributeValue(AID,id);
    }

    public Div setTitle(String title) {
      complete();
      return setAttributeValue(ATITLE,title);
    }

    public Div setXhtmlClass(String[] classes) {
        complete();
        if (classes != null) {
            StringBuilder val = new StringBuilder();
            for (String s : classes) {
                if (s.length() > 0)
                    val.append(" ");
                val.append(s);
            }
            setAttributeValue(CLASS, val.toString());
        } else
            removeAttribute(CLASS);
        return this;
    }

    public String getValue() {
        return getInternalValue();
    }

    public void setValue(String value) {
        complete();
        _removeAllChildren();
        if (value != null) {
            IRI baseUri = null;
            value = String.format("<div xmlns=\"%s\">%s</div>",XHTML_NS,value);
            OMElement element = null;
            try {
                baseUri = getResolvedBaseUri();
                element = (OMElement)_parse(value, baseUri);
            } catch (Exception e) {
            }
            List<Object> list = new ArrayList<Object>();
            for (Iterator<?> i = element.getChildren(); i.hasNext();)
                list.add(i.next());
            for (Object o : list) {
              OMNode node = (OMNode)o;
              node.detach();
              this.addChild(node);
            }
        }
    }

    protected String getInternalValue() {
        try {
            StringWriter out = new StringWriter();
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            writer.writeStartElement(""); 
            for (Iterator<?> nodes = this.getChildren(); nodes.hasNext();) {
                OMNode node = (OMNode)nodes.next();
                node.serialize(writer);
            }
            writer.writeEndElement(); 
            return out.getBuffer().toString().substring(2);
        } catch (Exception e) {
        }
        return "";
    }

}
