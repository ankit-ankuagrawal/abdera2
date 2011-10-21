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
package org.apache.abdera2.test.server.customer;

import java.util.Arrays;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContextException;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.protocol.server.context.AtompubRequestContext;
import org.apache.abdera2.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.joda.time.DateTime;

public class CustomerAdapter extends AbstractEntityCollectionAdapter<Customer> {
    private static final String ID_PREFIX = "urn:acme:customer:";

    private AtomicInteger nextId = new AtomicInteger(1000);
    private Map<Integer, Customer> customers = new HashMap<Integer, Customer>();
    private Factory factory = Abdera.getInstance().getFactory();

    public CustomerAdapter(String href) {
      super(href);
    }
    
    public String getId(RequestContext request) {
        return "tag:example.org,2007:feed";
    }

    @Override
    public Customer postEntry(String title,
                              IRI id,
                              String summary,
                              org.joda.time.DateTime updated,
                              List<Person> authors,
                              Content content,
                              RequestContext request) throws ResponseContextException {
        Customer customer = contentToCustomer(content);
        customers.put(customer.getId(), customer);

        return customer;
    }

    private Customer contentToCustomer(Content content) {
        Customer customer = new Customer();

        return contentToCustomer(content, customer);
    }

    private Customer contentToCustomer(Content content, Customer customer) {
        Element firstChild = content.getFirstChild();
        customer.setName(firstChild.getAttributeValue("name"));
        customer.setId(nextId.incrementAndGet());
        return customer;
    }

    public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Integer id = getIdFromResourceName(resourceName);
        customers.remove(id);
    }

    public String getAuthor(RequestContext request) {
        return "Acme Industries";
    }

    @Override
    public List<Person> getAuthors(Customer entry, RequestContext context) throws ResponseContextException {
        AtompubRequestContext request = (AtompubRequestContext) context;
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("Acme Industries");
        return Arrays.asList(author);
    }

    public Object getContent(Customer entry, RequestContext request) {
        Content content = factory.newContent();
        Element customerEl = factory.newElement(new QName("customer"));
        customerEl.setAttributeValue(new QName("name"), entry.getName());

        content.setValueElement(customerEl);
        return content;
    }

    public Iterable<Customer> getEntries(RequestContext request) {
        return customers.values();
    }

    public Customer getEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Integer id = getIdFromResourceName(resourceName);
        return customers.get(id);
    }

    private Integer getIdFromResourceName(String resourceName) throws ResponseContextException {
        int idx = resourceName.indexOf("-");
        if (idx == -1) {
            throw new ResponseContextException(404);
        }
        Integer id = new Integer(resourceName.substring(0, idx));
        return id;
    }

    public Customer getEntryFromId(String id, RequestContext request) {
        return customers.get(new Integer(id));
    }

    public String getId(Customer entry) {
        // TODO: is this valid?
        return ID_PREFIX + entry.getId();
    }

    public String getName(Customer entry) {
        return entry.getId() + "-" + entry.getName().replaceAll(" ", "_");
    }

    public String getTitle(RequestContext request) {
        return "Acme Customer Database";
    }

    public String getTitle(Customer entry) {
        return entry.getName();
    }

    public org.joda.time.DateTime getUpdated(Customer entry) {
        return org.joda.time.DateTime.now();
    }

    @Override
    public void putEntry(Customer entry,
                         String title,
                         DateTime updated,
                         List<Person> authors,
                         String summary,
                         Content content,
                         RequestContext request) throws ResponseContextException {
        contentToCustomer(content, entry);
    }

}
