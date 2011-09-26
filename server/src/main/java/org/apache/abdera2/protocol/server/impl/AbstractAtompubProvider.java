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
package org.apache.abdera2.protocol.server.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.date.DateTime;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.QualityHelper;
import org.apache.abdera2.common.http.QualityHelper.QToken;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.protocol.AbstractResponseContext;
import org.apache.abdera2.common.protocol.BaseProvider;
import org.apache.abdera2.common.protocol.CollectionRequestProcessor;
import org.apache.abdera2.common.protocol.EntryRequestProcessor;
import org.apache.abdera2.common.protocol.MediaRequestProcessor;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContext;
import org.apache.abdera2.common.protocol.Provider;
import org.apache.abdera2.common.protocol.ProviderHelper;
import org.apache.abdera2.common.protocol.TargetType;
import org.apache.abdera2.common.protocol.WorkspaceInfo;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.ExtensibleElement;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Service;
import org.apache.abdera2.protocol.error.Error;
import org.apache.abdera2.protocol.server.AtompubProvider;
import org.apache.abdera2.protocol.server.AtompubResponseContext;
import org.apache.abdera2.protocol.server.context.AtompubRequestContext;
import org.apache.abdera2.protocol.server.context.FOMResponseContext;
import org.apache.abdera2.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera2.protocol.server.model.AtompubWorkspaceInfo;
import org.apache.abdera2.protocol.server.processors.CategoriesRequestProcessor;
import org.apache.abdera2.protocol.server.processors.ServiceRequestProcessor;
import org.apache.abdera2.writer.StreamWriter;
import org.apache.abdera2.writer.Writer;
import org.apache.abdera2.writer.WriterFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base Provider implementation that provides the core implementation details for all Providers. This class provides the
 * basic request routing logic.
 */
public abstract class AbstractAtompubProvider 
  extends BaseProvider
  implements AtompubProvider {

    private final static Log log = LogFactory.getLog(AbstractAtompubProvider.class);
    protected Abdera abdera;

    protected AbstractAtompubProvider() {
        this.requestProcessors.put(TargetType.TYPE_SERVICE, new ServiceRequestProcessor());
        this.requestProcessors.put(TargetType.TYPE_CATEGORIES, new CategoriesRequestProcessor());
        this.requestProcessors.put(TargetType.TYPE_COLLECTION, new CollectionRequestProcessor() {
          protected boolean isAcceptableItemType(RequestContext context) {
            return ProviderHelper.isAtom(context);
          }
        });
        this.requestProcessors.put(TargetType.TYPE_ENTRY, new EntryRequestProcessor());
        this.requestProcessors.put(TargetType.TYPE_MEDIA, new MediaRequestProcessor());
    }

    public void init(Abdera abdera, Map<String, String> properties) {
        this.abdera = abdera == null ? abdera : Abdera.getInstance();
        this.init(properties);
    }

    public Abdera getAbdera() {
        return abdera;
    }

    protected Service getServiceElement(RequestContext request) {
        Service service = abdera.newService();
        for (WorkspaceInfo wi : getWorkspaceManager(request).getWorkspaces(request)) {
            if (wi instanceof AtompubWorkspaceInfo) {
              AtompubWorkspaceInfo awi = (AtompubWorkspaceInfo) wi;
              service.addWorkspace(awi.asWorkspaceElement(request));
            }
        }
        return service;
    }
    
    @SuppressWarnings("unchecked")
    public <S extends ResponseContext>S createErrorResponse(int code, String message, Throwable t) {
      return (S)createErrorResponse(abdera,code,message,t);
    }
    
    public static Abdera getAbdera(RequestContext context) {
      Provider provider = context.getProvider();
      return provider instanceof AtompubProvider ? 
        ((AtompubProvider)provider).getAbdera() : 
        Abdera.getInstance();
    }

    public static ResponseContext createErrorResponse(
        Abdera abdera,
        final int code, 
        final String message,
        final Throwable t) {
        AbstractResponseContext rc = new StreamWriterResponseContext(abdera) {
          @Override
          protected void writeTo(StreamWriter sw) throws IOException {
              Error.create(sw, code, message, t);
          }
        };
        rc.setStatus(code);
        rc.setStatusText(message);
        return rc;
      }

    /**
     * Check to see if the entry is minimally valid according to RFC4287. This is not a complete check. It just verifies
     * that the appropriate elements are present and that their values can be accessed.
     */
    public static boolean isValidEntry(Entry entry) {
        try {
            IRI id = entry.getId();
            if (id == null || id.toString().trim().length() == 0 || !id.isAbsolute())
                return false;
            if (entry.getTitle() == null)
                return false;
            if (entry.getUpdated() == null)
                return false;
            if (entry.getAuthor() == null && (entry.getSource() != null && entry.getSource().getAuthor() == null))
                return false;
            Content content = entry.getContentElement();
            if (content == null) {
                if (entry.getAlternateLink() == null)
                    return false;
            } else {
                if ((content.getSrc() != null || content.getContentType() == Content.Type.MEDIA) && entry
                    .getSummaryElement() == null) {
                    ProviderHelper.log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", false));
                    return false;
                }
            }
        } catch (Exception e) {
            ProviderHelper.log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", false));
            return false;
        }
        ProviderHelper.log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", true));
        return true;
    }

    /**
     * Return false if the element contains any extension elements that are not supported
     */
    public static boolean checkElementNamespaces(Element element, List<String> ignore) {
        List<QName> attrs = element.getExtensionAttributes();
        for (QName qname : attrs) {
            String ns = qname.getNamespaceURI();
            if (!ignore.contains(ns))
                return false;
        }
        if (element instanceof ExtensibleElement) {
            ExtensibleElement ext = (ExtensibleElement)element;
            for (Element el : ext.getExtensions()) {
                QName qname = el.getQName();
                String ns = qname.getNamespaceURI();
                if (!ignore.contains(ns))
                    return false;
                if (!checkElementNamespaces(el, ignore))
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns an appropriate NamedWriter instance given an appropriately formatted HTTP Accept header. The header will
     * be parsed and sorted according to it's q parameter values. The first named writer capable of supporting the
     * specified type, in order of q-value preference, will be returned. The results on this are not always predictable.
     * For instance, if the Accept header says "application/*" it could end up with either the JSON writer or the
     * PrettyXML writer, or any other writer that supports any writer that supports a specific form of "application/*".
     * It's always best to be very specific in the Accept headers.
     */
    public static Writer getAcceptableNamedWriter(Abdera abdera, String accept_header) {
        QToken[] sorted_accepts = QualityHelper.orderByQ(accept_header);
        WriterFactory factory = abdera.getWriterFactory();
        if (factory == null)
            return null;
        for (QToken accept : sorted_accepts) {
            Writer writer = factory.getWriterByMediaType(accept.token());
            if (writer != null)
                return writer;
        }
        return null;
    }

    public static Writer getNamedWriter(Abdera abdera, String mediatype) {
        WriterFactory factory = abdera.getWriterFactory();
        if (factory == null)
            return null;
        Writer writer = factory.getWriterByMediaType(mediatype);
        return writer;
    }

    public static EntityTag calculateEntityTag(Base base) {
        String id = null;
        String modified = null;
        if (base instanceof Entry) {
            Entry entry = (Entry)base;
            id = entry.getId().toString();
            modified = DateTime.format(entry.getEdited() != null ? entry.getEdited() : entry.getUpdated());
        } else if (base instanceof Feed) {
            Feed feed = (Feed)base;
            id = feed.getId().toString();
            modified = DateTime.format(feed.getUpdated());
        } else if (base instanceof Document) {
            return calculateEntityTag(((Document<?>)base).getRoot());
        }
        return EntityTag.generate(id, modified);
    }

    public static String getEditUriFromEntry(Entry entry) {
        String editUri = null;
        List<Link> editLinks = entry.getLinks("edit");
        if (editLinks != null) {
            for (Link link : editLinks) {
                // if there is more than one edit link, we should not automatically
                // assume that it's always going to point to an Atom document
                // representation.
                if (link.getMimeType() != null) {
                    if (MimeTypeHelper.isMatch(link.getMimeType().toString(), Constants.ATOM_MEDIA_TYPE)) {
                        editUri = link.getResolvedHref().toString();
                        break;
                    }
                } else {
                    // edit link with no type attribute is the right one to use
                    editUri = link.getResolvedHref().toString();
                    break;
                }
            }
        }
        return editUri;
    }

    /**
     * Return a document
     */
    public static AtompubResponseContext returnBase(Base base, int status, Date lastModified) {
        log.debug(Localizer.get("RETURNING.DOCUMENT"));
        FOMResponseContext<Base> response = new FOMResponseContext<Base>(base);
        response.setStatus(status);
        if (lastModified != null)
            response.setLastModified(lastModified);
        // response.setContentType(MimeTypeHelper.getMimeType(base));
        Document<?> doc = base instanceof Document ? (Document<?>)base : ((Element)base).getDocument();
        if (doc.getEntityTag() != null) {
            response.setEntityTag(doc.getEntityTag());
        } else if (doc.getLastModified() != null) {
            response.setLastModified(doc.getLastModified());
        }
        return response;
    }

    @Override
    public <S extends ResponseContext> S process(RequestContext request) {
      return (S)super.process(request instanceof AtompubRequestContext?request:new AtompubRequestContext(request));
    }
}
