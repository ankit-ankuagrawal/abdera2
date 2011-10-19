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

import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.model.Collection;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.model.Workspace;
import org.apache.abdera2.model.selector.CollectionAcceptSelector;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings({"deprecation","rawtypes"})
public class FOMWorkspace extends FOMExtensibleElement implements Workspace {

    private static final long serialVersionUID = -421749865550509424L;

    public FOMWorkspace() {
        super(Constants.WORKSPACE);
    }

    public FOMWorkspace(String title) {
        this();
        setTitle(title);
    }

    public FOMWorkspace(String name, OMNamespace namespace, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMWorkspace(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    public FOMWorkspace(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(qname, parent, factory, builder);
    }

    public FOMWorkspace(OMContainer parent, OMFactory factory) throws OMException {
        super(WORKSPACE, parent, factory);
    }

    public FOMWorkspace(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(WORKSPACE, parent, factory, builder);
    }

    public String getTitle() {
        Text title = this.getFirstChild(TITLE);
        return (title != null) ? title.getValue() : null;
    }

    private Text setTitle(String title, Text.Type type) {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Text text = fomfactory.newText(PREFIXED_TITLE, type);
        text.setValue(title);
        this._setChild(PREFIXED_TITLE, (OMElement)text);
        return text;
    }

    public Text setTitle(String title) {
        return setTitle(title, Text.Type.TEXT);
    }

    public Text setTitleAsHtml(String title) {
        return setTitle(title, Text.Type.HTML);
    }

    public Text setTitleAsXHtml(String title) {
        return setTitle(title, Text.Type.XHTML);
    }

    public Text getTitleElement() {
        return getFirstChild(TITLE);
    }

    public List<Collection> getCollections() {
        List<Collection> list = _getChildrenAsSet(COLLECTION);
        if (list == null || list.size() == 0)
            list = _getChildrenAsSet(PRE_RFC_COLLECTION);
        return list;
    }

    public Collection getCollection(String title) {
        List<Collection> cols = getCollections();
        Collection col = null;
        for (Collection c : cols) {
            if (c.getTitle().equals(title)) {
                col = c;
                break;
            }
        }
        return col;
    }

    public Workspace addCollection(Collection collection) {
        complete();
        addChild((OMElement)collection);
        return this;
    }

    public Collection addCollection(String title, String href) {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Collection collection = fomfactory.newCollection(this);
        collection.setTitle(title);
        collection.setHref(href);
        return collection;
    }

    public Collection addMultipartCollection(String title, String href) {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Collection collection = fomfactory.newMultipartCollection(this);
        collection.setTitle(title);
        collection.setHref(href);
        return collection;
    }

    public Collection getCollectionThatAccepts(MimeType... types) {
      CollectionAcceptSelector sel =
        new CollectionAcceptSelector(types);
      List<Collection> list = getCollections(sel);
      return list.size() > 0 ? list.get(0) : null;
    }

    public Collection getCollectionThatAccepts(String... types) {
      CollectionAcceptSelector sel =
        new CollectionAcceptSelector(types);
      List<Collection> list = getCollections(sel);
      return list.size() > 0 ? list.get(0) : null;
    }

    public List<Collection> getCollectionsThatAccept(MimeType... types) {
      CollectionAcceptSelector sel =
        new CollectionAcceptSelector(types);
      return getCollections(sel);
    }

    public List<Collection> getCollectionsThatAccept(String... types) {
      CollectionAcceptSelector sel =
        new CollectionAcceptSelector(types);
      return getCollections(sel);
    }

    public List<Collection> getCollections(Selector selector) {
      List<Collection> list = _getChildrenAsSet(COLLECTION,selector);
      if (list == null || list.size() == 0)
          list = _getChildrenAsSet(PRE_RFC_COLLECTION,selector);
      return list;
    }

}
