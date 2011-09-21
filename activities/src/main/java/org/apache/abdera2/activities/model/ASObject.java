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
package org.apache.abdera2.activities.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.abdera2.activities.model.objects.Mood;
import org.apache.abdera2.activities.model.objects.PersonObject;
import org.apache.abdera2.activities.model.objects.PlaceObject;
import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.iri.IRI;

/**
 * Base class for all Activity Streams Objects.
 */
@SuppressWarnings("unchecked")
public class ASObject extends ASBase {

  private static final long serialVersionUID = -6969558559101109831L;
  public static final String ATTACHMENTS = "attachments";
  public static final String AUTHOR = "author";
  public static final String CONTENT = "content";
  public static final String DISPLAYNAME = "displayName";
  public static final String DOWNSTREAMDUPLICATES = "downstreamDuplicates";
  public static final String ID = "id";
  public static final String IMAGE = "image";
  public static final String OBJECTTYPE = "objectType";
  public static final String PUBLISHED = "published";
  public static final String SUMMARY = "summary";
  public static final String UPDATED = "updated";
  public static final String UPSTREAMDUPLICATES = "upstreamDuplicates";
  public static final String URL = "url";
  
  public static final String INREPLYTO = "inReplyTo";
  public static final String LOCATION = "location";
  public static final String SOURCE = "source";
  public static final String MOOD = "mood";
  public static final String TAGS = "tags";
  public static final String RATING = "rating";
  
  public static final String EMBED = "embed";
  
  public ASObject() {
    setObjectType(AnnoUtil.getName(this));
  }
  
  public ASObject(String objectType) {
    setObjectType(objectType);
  }
  
  public Iterable<ASObject> getAttachments() {
    return getProperty(ATTACHMENTS);
  }
  
  public void setAttachments(Set<ASObject> attachments) {
    setProperty(ATTACHMENTS, attachments);
  }
  
  public void addAttachment(ASObject... attachments) {
    Set<ASObject> list = getProperty(ATTACHMENTS);
    if (list == null) {
      list = new HashSet<ASObject>();
      setProperty(ATTACHMENTS, list);
    }
    for (ASObject attachment : attachments)
      list.add(attachment); 
  }
  
  public <E extends ASObject>E getAuthor() {
    return (E)getProperty(AUTHOR);
  }
  
  public <E extends ASObject>E getAuthor(boolean create) {
    ASObject obj = getAuthor();
    if (obj == null && create) {
      obj = new PersonObject();
      setAuthor(obj);
    }
    return (E)obj;
  }
  
  public void setAuthor(ASObject author) {
    setProperty(AUTHOR, author);
  }
  
  public <E extends ASObject>E setAuthor(String displayName) {
    ASObject obj = getAuthor(true);
    obj.setDisplayName(displayName);
    return (E)obj;
  }
  
  public String getContent() {
    return getProperty(CONTENT);
  }
  
  public void setContent(String content) {
    setProperty(CONTENT, content);
    
  }
  
  public String getDisplayName() {
    return getProperty(DISPLAYNAME);
  }
  
  public void setDisplayName(String displayName) {
    setProperty(DISPLAYNAME, displayName);
    
  }
  
  public Iterable<String> getDownstreamDuplicates() {
    return getProperty(DOWNSTREAMDUPLICATES);
  }
  
  public void setDownstreamDuplicates(Set<String> downstreamDuplicates) {
    setProperty(DOWNSTREAMDUPLICATES, downstreamDuplicates);
    
  }
  
  public void addDownstreamDuplicate(String... duplicates) {
    Set<String> downstreamDuplicates = getProperty(DOWNSTREAMDUPLICATES);
    if (downstreamDuplicates == null) {
      downstreamDuplicates = new HashSet<String>();
      setProperty(DOWNSTREAMDUPLICATES, downstreamDuplicates);
    }
    for (String downstreamDuplicate : duplicates)
      downstreamDuplicates.add(downstreamDuplicate);  
  }
  
  public String getId() {
    return getProperty(ID);
  }
  
  public void setId(String id) {
    setProperty(ID, id);
    
  }
  
  public MediaLink getImage() {
    return getProperty(IMAGE);
  }
  
  public void setImage(MediaLink image) {
    setProperty(IMAGE, image);
    
  }
  
  public String getObjectType() {
    return getProperty(OBJECTTYPE);
  }
  
  public void setObjectType(String objectType) {
    if (objectType != null && ASObject.class.getSimpleName().equalsIgnoreCase(objectType))
      objectType = null;
    setProperty(OBJECTTYPE, objectType);
    
  }
  
  public Date getPublished() {
    return getProperty(PUBLISHED);
  }
  
  public void setPublished(Date published) {
    setProperty(PUBLISHED, published);
    
  }
  
  public String getSummary() {
    return getProperty(SUMMARY);
  }
  
  public void setSummary(String summary) {
    setProperty(SUMMARY, summary);
    
  }
  
  public Date getUpdated() {
    return getProperty(UPDATED);
  }
  
  public void setUpdated(Date updated) {
    setProperty(UPDATED, updated);
    
  }
  
  public Iterable<String> getUpstreamDuplicates() {
    return getProperty(UPSTREAMDUPLICATES);
  }
  
  public void setUpstreamDuplicates(Set<String> upstreamDuplicates) {
    setProperty(UPSTREAMDUPLICATES, upstreamDuplicates);
    
  }
  
  public void addUpstreamDuplicate(String... duplicates) {
    Set<String> upstreamDuplicates = getProperty(UPSTREAMDUPLICATES);
    if (upstreamDuplicates == null) {
      upstreamDuplicates = new HashSet<String>();
      setProperty(UPSTREAMDUPLICATES, upstreamDuplicates);
    }
    for (String upstreamDuplicate : duplicates)
      upstreamDuplicates.add(upstreamDuplicate);
  }
  
  
  public IRI getUrl() {
    return getProperty(URL);
  }
  
  public void setUrl(IRI url) {
    setProperty(URL,url);
  }
  
  public Iterable<ASObject> getInReplyTo() {
    return getProperty(INREPLYTO);
  }
  
  public void setInReplyTo(Set<ASObject> inReplyTo) {
    setProperty(INREPLYTO, inReplyTo);
  }
  
  public void addInReplyTo(ASObject... inReplyTos) {
    Set<ASObject> list = getProperty(INREPLYTO);
    if (list == null) {
      list = new HashSet<ASObject>();
      setProperty(INREPLYTO, list);
    }
    for (ASObject inReplyTo : inReplyTos)
      list.add(inReplyTo);
  }
  
  public PlaceObject getLocation() {
    return getProperty(LOCATION);
  }
  
  public void setLocation(PlaceObject location) {
    setProperty(LOCATION, location);
    location.setObjectType(null);
  }
  
  public Mood getMood() {
    return getProperty(MOOD);
  }
  
  public void setMood(Mood mood) {
    setProperty(MOOD, mood);
  }
  
  public <E extends ASObject>E getSource() {
    return (E)getProperty(SOURCE);
  }
  
  public void setSource(ASObject source) {
    setProperty(SOURCE, source);
  }

  public Iterable<ASObject> getTags() {
    return getProperty(TAGS);
  }
  
  public void setTags(Set<ASObject> tags) {
    setProperty(TAGS, tags);
  }
  
  public void addTag(ASObject... tags) {
    Set<ASObject> list = getProperty(TAGS);
    if (list == null) {
      list = new HashSet<ASObject>();
      setProperty(TAGS, list);
    }
    for (ASObject tag : tags)
      list.add(tag); 
  }
  
  /**
   * @see org.apache.abdera2.activities.model.ASObject.getEmbeddedExperience()
   * {"embed":{...}}
   */
  public <T extends ASObject>T getEmbed() {
    return getProperty(EMBED);
  }
  
  /**
   * @see org.apache.abdera2.activities.model.ASObject.setEmbeddedExperience()
   * {"embed":{...}}
   */
  public void setEmbed(ASObject embed) {
    setProperty(EMBED,embed);
  }
  
  public double getRating() {
    return (Double)getProperty(RATING);
  }
  
  public void setRating(double rating) {
    setProperty(RATING, rating);
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String objectType = getObjectType();
    String displayName = getDisplayName();
    if (displayName != null)
      sb.append(displayName);
    else if (objectType != null) {
      char s = objectType.charAt(0);
      if ("aeiou".indexOf(s) > -1) 
        sb.append("an ");
      else sb.append("a ");
      sb.append(objectType);
    } else {
      sb.append("an object");
    }
    return sb.toString();
  }

  /**
   * "Embedded Experiences" were introduced to Activity Streams 
   * by the OpenSocial 2.0 specification, while functionally not 
   * specific to OpenSocial, the spec defines that the "Embedded 
   * Experience" document has to be wrapped within an "openSocial"
   * extension property. Other applications, such as Google+, however,
   * use the "embed" property directly within an object without the
   * "openSocial" wrapper. To use the "embed" property without 
   * the "openSocial" wrapper, use the setEmbed/getEmbed properties
   * on ASObject. To use OpenSocial style Embedded Experiences, 
   * use the setEmbeddedExperience/getEmbeddedExperience/hasEmbeddedExperience
   * methods. The OpenSocial style Embedded Experience should be used
   * primarily to associate OpenSocial Gadgets with an activity 
   * object while the alternative "embed" can be used to reference
   * any kind of embedded content.
   * 
   * {"openSocial":{"embed":{...}}}
   */
  public void setEmbeddedExperience(EmbeddedExperience embed) {
    ASBase os = getProperty("openSocial");
    if (os == null) {
      os = new ASBase();
      setProperty("openSocial", os);
    }
    os.setProperty("embed", embed);
  }
  
  /**
   * "Embedded Experiences" were introduced to Activity Streams 
   * by the OpenSocial 2.0 specification, while functionally not 
   * specific to OpenSocial, the spec defines that the "Embedded 
   * Experience" document has to be wrapped within an "openSocial"
   * extension property. Other applications, such as Google+, however,
   * use the "embed" property directly within an object without the
   * "openSocial" wrapper. To use the "embed" property without 
   * the "openSocial" wrapper, use the setEmbed/getEmbed properties
   * on ASObject. To use OpenSocial style Embedded Experiences, 
   * use the setEmbeddedExperience/getEmbeddedExperience/hasEmbeddedExperience
   * methods. The OpenSocial style Embedded Experience should be used
   * primarily to associate OpenSocial Gadgets with an activity 
   * object while the alternative "embed" can be used to reference
   * any kind of embedded content.
   * 
   * {"openSocial":{"embed":{...}}}
   */
  public EmbeddedExperience getEmbeddedExperience() {
    if (!has("openSocial")) return null;
    ASBase os = getProperty("openSocial");
    if (!os.has("embed")) return null;
    ASBase e = os.getProperty("embed");
    if (!(e instanceof EmbeddedExperience)) {
      e = e.as(EmbeddedExperience.class);
      os.setProperty("embed", e);
    }
    return (EmbeddedExperience) e;
  }
  
  /**
   * Checks to see if the "openSocial":{"embed":{...}} property exists
   */
  public boolean hasEmbeddedExperience() {
    if (!has("openSocial")) return false;
    ASBase os = getProperty("openSocial");
    return os.has("embed");
  }
}
