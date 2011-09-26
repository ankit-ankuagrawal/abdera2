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
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.text.UrlEncoding;
import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.date.DateTime;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.parser.ParseException;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContext;
import org.apache.abdera2.common.protocol.EmptyResponseContext;
import org.apache.abdera2.common.protocol.MediaResponseContext;
import org.apache.abdera2.common.protocol.ProviderHelper;
import org.apache.abdera2.common.protocol.ResponseContextException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * By extending this class it becomes easy to build Collections which are backed by a set of entities - such as a
 * database row, domain objects, or files.
 * 
 * @param <T> The entity that this is backed by.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractEntityCollectionAdapter<T> 
  extends AbstractAtompubCollectionAdapter {

  private final static Log log = LogFactory.getLog(AbstractEntityCollectionAdapter.class);

    /**
     * Create a new entry
     * 
     * @param title The title of the entry (assumes that type="text")
     * @param id The value of the atom:id element
     * @param summary The summary of the entry
     * @param updated The value of the atom:updated element
     * @param authors Listing of atom:author elements
     * @param context The content of the entry
     * @param request The request context
     */
    public abstract T postEntry(String title,
                                IRI id,
                                String summary,
                                Date updated,
                                List<Person> authors,
                                Content content,
                                RequestContext request) throws ResponseContextException;

    @Override
    public <S extends ResponseContext>S postMedia(RequestContext request) {
        return (S)createMediaEntry(request);
    }

    @Override
    public <S extends ResponseContext>S putMedia(RequestContext request) {
        try {
            String id = getResourceName(request);
            T entryObj = getEntry(id, request);

            putMedia(entryObj, request.getContentType(), request.getSlug(), request.getInputStream(), request);

            return (S)new EmptyResponseContext(200);
        } catch (IOException e) {
            return (S)new EmptyResponseContext(500);
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    /**
     * Update a media resource. By default this method is not allowed. Implementations must override this method to
     * support media resource updates
     * 
     * @param entryObj
     * @param contentType The mime-type of the media resource
     * @param slug The value of the Slug request header
     * @param inputStream An input stream providing access to the request payload
     * @param request The request context
     */
    public void putMedia(T entryObj, MimeType contentType, String slug, InputStream inputStream, RequestContext request)
        throws ResponseContextException {
        throw new ResponseContextException(ProviderHelper.notallowed(request));
    }

    public <S extends ResponseContext>S postItem(RequestContext request) {
        return (S)createNonMediaEntry(request);
    }

    protected String getLink(T entryObj, IRI feedIri, RequestContext request) throws ResponseContextException {
        return getLink(entryObj, feedIri, request, false);
    }

    protected String getLink(T entryObj, IRI feedIri, RequestContext request, boolean absolute)
        throws ResponseContextException {
        return getLink(getName(entryObj), entryObj, feedIri, request, absolute);
    }

    protected String getLink(String name, T entryObj, IRI feedIri, RequestContext request) {
        return getLink(name, entryObj, feedIri, request, false);
    }

    protected String getLink(String name, T entryObj, IRI feedIri, RequestContext request, boolean absolute) {
        feedIri = feedIri.trailingSlash();
        IRI entryIri = feedIri.resolve(UrlEncoding.encode(name, Profile.PATH));

        if (absolute) {
            entryIri = request.getResolvedUri().resolve(entryIri);
        }

        String link = entryIri.toString();

        String qp = getQueryParameters(entryObj, request);
        if (qp != null && !"".equals(qp)) {
            StringBuilder sb = new StringBuilder();
            sb.append(link).append("?").append(qp);
            link = sb.toString();
        }

        return link;
    }

    protected String getQueryParameters(T entryObj, RequestContext request) {
        return null;
    }

    /**
     * Post a new media resource to the collection. By default, this method is not supported. Implementations must
     * override this method to support posting media resources
     * 
     * @param mimeType The mime-type of the resource
     * @param slug The value of the Slug header
     * @param inputStream An InputStream providing access to the request payload
     * @param request The request context
     */
    public T postMedia(MimeType mimeType, String slug, InputStream inputStream, RequestContext request)
        throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    public <S extends ResponseContext>S deleteItem(RequestContext request) {
        String id = getResourceName(request);
        if (id != null) {

            try {
                deleteEntry(id, request);
            } catch (ResponseContextException e) {
                return (S)createErrorResponse(e);
            }

            return (S)new EmptyResponseContext(204);
        } else {
            return (S)new EmptyResponseContext(404);
        }
    }

    /**
     * Delete an entry
     * 
     * @param resourceName The entry to delete
     * @param request The request context
     */
    public abstract void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException;

    public <S extends ResponseContext>S deleteMedia(RequestContext request) {
        String resourceName = getResourceName(request);
        if (resourceName != null) {

            try {
                deleteMedia(resourceName, request);
            } catch (ResponseContextException e) {
                return (S)createErrorResponse(e);
            }

            return (S)new EmptyResponseContext(204);
        } else {
            return (S)new EmptyResponseContext(404);
        }
    }

    /**
     * Delete a media resource. By default this method is not supported. Implementations must override this method to
     * support deleting media resources
     */
    public void deleteMedia(String resourceName, RequestContext request) throws ResponseContextException {
        throw new ResponseContextException(ProviderHelper.notsupported(request));
    }

    /**
     * Get the authors for an entry. By default this returns null. Implementations must override in order to providing a
     * listing of authors for an entry
     */
    public List<Person> getAuthors(T entry, RequestContext request) throws ResponseContextException {
        return null;
    }

    /**
     * Get the content for the entry.
     */
    public abstract Object getContent(T entry, RequestContext request) throws ResponseContextException;

    // GET, POST, PUT, DELETE

    /**
     * Get the content-type for the entry. By default this operation is not supported.
     */
    public String getContentType(T entry) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the listing of entries requested
     */
    public abstract Iterable<T> getEntries(RequestContext request) throws ResponseContextException;

    public <S extends ResponseContext>S getItem(RequestContext request) {
        try {
            Entry entry = getEntryFromCollectionProvider(request);
            if (entry != null) {
                return (S)buildGetEntryResponse(request, entry);
            } else {
                return (S)new EmptyResponseContext(404);
            }
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    /**
     * Get a specific entry
     * 
     * @param resourceName The entry to get
     * @param request The request context
     */
    public abstract T getEntry(String resourceName, RequestContext request) throws ResponseContextException;

    public <S extends ResponseContext>S headItem(RequestContext request) {
        try {
            String resourceName = getResourceName(request);
            T entryObj = getEntry(resourceName, request);

            if (entryObj != null) {
                return (S)buildHeadEntryResponse(request, resourceName, getUpdated(entryObj));
            } else {
                return (S)new EmptyResponseContext(404);
            }
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    public <S extends ResponseContext>S headMedia(RequestContext request) {
        try {
            String resourceName = getResourceName(request);
            T entryObj = getEntry(resourceName, request);

            if (entryObj != null) {
                return (S)buildHeadEntryResponse(request, resourceName, getUpdated(entryObj));
            } else {
                return (S)new EmptyResponseContext(404);
            }
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    public <S extends ResponseContext>S getItemList(RequestContext request) {
        try {
            Feed feed = createFeedBase(request);

            addFeedDetails(feed, request);

            return (S)buildGetFeedResponse(feed);
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    /**
     * Adds the selected entries to the Feed document. By default, this will set the feed's atom:updated element to the
     * current date and time
     */
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        feed.setUpdated(new Date());

        Iterable<T> entries = getEntries(request);
        if (entries != null) {
            for (T entryObj : entries) {
                Entry e = feed.addEntry();

                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);

                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                }
            }
        }
    }

    private IRI getFeedIRI(T entryObj, RequestContext request) {
        String feedIri = getFeedIriForEntry(entryObj, request);
        return new IRI(feedIri).trailingSlash();
    }

    /**
     * Gets the UUID for the specified entry.
     * 
     * @param entry
     * @return
     */
    public abstract String getId(T entry) throws ResponseContextException;

    public <S extends ResponseContext>S getMedia(RequestContext request) {
        try {
            String resource = getResourceName(request);
            T entryObj = getEntry(resource, request);

            if (entryObj == null) {
                return (S)new EmptyResponseContext(404);
            }

            return (S)buildGetMediaResponse(resource, entryObj);
        } catch (ParseException pe) {
            return (S)new EmptyResponseContext(415);
        } catch (ClassCastException cce) {
            return (S)new EmptyResponseContext(415);
        } catch (ResponseContextException e) {
            return (S)e.getResponseContext();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return (S)new EmptyResponseContext(400);
        }
    }

    /**
     * Creates a ResponseContext for a GET media request. By default, this returns a MediaResponseContext containing the
     * media resource. The last-modified header will be set.
     */
    protected <S extends ResponseContext>S buildGetMediaResponse(String id, T entryObj) throws ResponseContextException {
        Date updated = getUpdated(entryObj);
        MediaResponseContext ctx = new MediaResponseContext(getMediaStream(entryObj), updated, 200);
        ctx.setContentType(getContentType(entryObj));
        ctx.setEntityTag(EntityTag.generate(id, DateTime.format(updated)));
        return (S)ctx;
    }

    /**
     * Get the name of the media resource. By default this method is unsupported. Implementations must override.
     */
    public String getMediaName(T entry) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get an input stream for the media resource. By default this method is unsupported. Implementations must override.
     */
    public InputStream getMediaStream(T entry) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the name of the entry resource (used to construct links)
     */
    public abstract String getName(T entry) throws ResponseContextException;

    /**
     * Get the title fo the entry
     */
    public abstract String getTitle(T entry) throws ResponseContextException;

    /**
     * Get the value to use in the atom:updated element
     */
    public abstract Date getUpdated(T entry) throws ResponseContextException;

    /**
     * True if this entry is a media-link entry. By default this always returns false. Implementations must override to
     * support media link entries
     */
    public boolean isMediaEntry(T entry) throws ResponseContextException {
        return false;
    }

    public <S extends ResponseContext>S putItem(RequestContext request) {
        try {
            String id = getResourceName(request);
            T entryObj = getEntry(id, request);

            if (entryObj == null) {
                return (S)new EmptyResponseContext(404);
            }

            Entry orig_entry =
                getEntryFromCollectionProvider(entryObj, new IRI(getFeedIriForEntry(entryObj, request)), request);
            if (orig_entry != null) {

                MimeType contentType = request.getContentType();
                if (contentType != null && !MimeTypeHelper.isAtom(contentType.toString()))
                    return (S)new EmptyResponseContext(415);

                Entry entry = getEntryFromRequest(request);
                if (entry != null) {
                    if (!entry.getId().equals(orig_entry.getId()))
                        return (S)new EmptyResponseContext(409);

                    if (!AbstractAtompubProvider.isValidEntry(entry))
                        return (S)new EmptyResponseContext(400);

                    putEntry(entryObj, entry.getTitle(), new Date(), entry.getAuthors(), entry.getSummary(), entry
                        .getContentElement(), request);
                    return (S)new EmptyResponseContext(204);
                } else {
                    return (S)new EmptyResponseContext(400);
                }
            } else {
                return (S)new EmptyResponseContext(404);
            }
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        } catch (ParseException pe) {
            return (S)new EmptyResponseContext(415);
        } catch (ClassCastException cce) {
            return (S)new EmptyResponseContext(415);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return (S)new EmptyResponseContext(400);
        }

    }

    /**
     * Get the Feed IRI
     */
    protected String getFeedIriForEntry(T entryObj, RequestContext request) {
        return getHref(request);
    }

    /**
     * Update an entry.
     * 
     * @param entry The entry to update
     * @param title The new title of the entry
     * @param updated The new value of atom:updated
     * @param authors To new listing of authors
     * @param summary The new summary
     * @param content The new content
     * @param request The request context
     */
    public abstract void putEntry(T entry,
                                  String title,
                                  Date updated,
                                  List<Person> authors,
                                  String summary,
                                  Content content,
                                  RequestContext request) throws ResponseContextException;

    /**
     * Adds the atom:content element to an entry
     */
    protected void addContent(Entry e, T doc, RequestContext request) throws ResponseContextException {
        Object content = getContent(doc, request);

        if (content instanceof Content) {
            e.setContentElement((Content)content);
        } else if (content instanceof String) {
            e.setContent((String)content);
        }
    }

    /**
     * Add the details to an entry
     * 
     * @param request The request context
     * @param e The entry
     * @param feedIri The feed IRI
     * @param entryObj
     */
    protected String addEntryDetails(RequestContext request, Entry e, IRI feedIri, T entryObj)
        throws ResponseContextException {
        String link = getLink(entryObj, feedIri, request);

        e.addLink(link, "edit");
        e.setId(getId(entryObj));
        e.setTitle(getTitle(entryObj));
        e.setUpdated(getUpdated(entryObj));

        List<Person> authors = getAuthors(entryObj, request);
        if (authors != null) {
            for (Person a : authors) {
                e.addAuthor(a);
            }
        }

        Text t = getSummary(entryObj, request);
        if (t != null) {
            e.setSummaryElement(t);
        }
        return link;
    }

    /**
     * Get the summary of the entry. By default this returns null.
     */
    public Text getSummary(T entry, RequestContext request) throws ResponseContextException {
        return null;
    }

    /**
     * Add media content details to a media-link entry
     * 
     * @param feedIri The feed iri
     * @param entry The entry object
     * @param entryObj
     * @param request The request context
     */
    protected String addMediaContent(IRI feedIri, Entry entry, T entryObj, RequestContext request)
        throws ResponseContextException {
        String name = getMediaName(entryObj);

        IRI mediaIri = new IRI(getLink(name, entryObj, feedIri, request));
        String mediaLink = mediaIri.toString();
        entry.setContent(mediaIri, getContentType(entryObj));
        entry.addLink(mediaLink, "edit-media");

        return mediaLink;
    }

    /**
     * Create a media entry
     * 
     * @param request The request context
     */
    protected <S extends ResponseContext>S createMediaEntry(RequestContext request) {
        try {
            T entryObj = postMedia(request.getContentType(), request.getSlug(), request.getInputStream(), request);

            IRI feedUri = getFeedIRI(entryObj, request);

            Entry entry = AbstractAtompubProvider.getAbdera(request).getFactory().newEntry();
            addEntryDetails(request, entry, feedUri, entryObj);
            addMediaContent(feedUri, entry, entryObj, request);

            String location = getLink(entryObj, feedUri, request, true);
            return (S)buildPostMediaEntryResponse(location, entry);
        } catch (IOException e) {
            return (S)new EmptyResponseContext(500);
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    /**
     * Create a regular entry
     * 
     * @param request The request context
     */
    protected <S extends ResponseContext>S createNonMediaEntry(RequestContext request) {
        try {
            Entry entry = getEntryFromRequest(request);
            if (entry != null) {
                if (!AbstractAtompubProvider.isValidEntry(entry))
                    return (S)new EmptyResponseContext(400);

                entry.setUpdated(new Date());

                T entryObj =
                    postEntry(entry.getTitle(), entry.getId(), entry.getSummary(), entry.getUpdated(), entry
                        .getAuthors(), entry.getContentElement(), request);

                entry.getIdElement().setValue(getId(entryObj));

                IRI feedUri = getFeedIRI(entryObj, request);

                String link = getLink(entryObj, feedUri, request);
                entry.addLink(link, "edit");

                String location = getLink(entryObj, feedUri, request, true);
                return (S)buildCreateEntryResponse(location, entry);
            } else {
                return (S)new EmptyResponseContext(400);
            }
        } catch (ResponseContextException e) {
            return (S)createErrorResponse(e);
        }
    }

    protected Entry getEntryFromCollectionProvider(RequestContext request) throws ResponseContextException {
        String id = getResourceName(request);
        T entryObj = getEntry(id, request);

        if (entryObj == null) {
            return null;
        }

        IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
        return getEntryFromCollectionProvider(entryObj, feedIri, request);
    }

    Entry getEntryFromCollectionProvider(T entryObj, IRI feedIri, RequestContext request)
        throws ResponseContextException {
        Abdera abdera = AbstractAtompubProvider.getAbdera(request);
        Factory factory = abdera.getFactory();
        Entry entry = factory.newEntry();

        return buildEntry(entryObj, entry, feedIri, request);
    }

    /**
     * Build the entry from the source object
     * 
     * @param entryObj The source object
     * @param entry The entry to build
     * @param feedIri The feed IRI
     * @param request The request context
     */
    private Entry buildEntry(T entryObj, Entry entry, IRI feedIri, RequestContext request)
        throws ResponseContextException {
        addEntryDetails(request, entry, feedIri, entryObj);

        if (isMediaEntry(entryObj)) {
            addMediaContent(feedIri, entry, entryObj, request);
        } else {
            addContent(entry, entryObj, request);
        }

        return entry;
    }

}
