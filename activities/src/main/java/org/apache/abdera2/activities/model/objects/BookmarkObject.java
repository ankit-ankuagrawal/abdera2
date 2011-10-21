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
package org.apache.abdera2.activities.model.objects;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;

@Name("bookmark")
public class BookmarkObject 
  extends ASObject {

  private static final long serialVersionUID = -2753772783838463022L;
  public static final String TARGETURL = "targetUrl";
  
  public BookmarkObject() {}
  
  public BookmarkObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public String getTargetUrl() {
    return getProperty(TARGETURL);
  }
  
  public void setTargetUrl(String targetUrl) {
    setProperty(TARGETURL, targetUrl);
  }

  public static <T extends BookmarkObject>BookmarkObjectGenerator<T> makeBookmark() {
    return new BookmarkObjectGenerator<T>();
  }
  
  @SuppressWarnings("unchecked")
  public static class BookmarkObjectGenerator<T extends BookmarkObject> extends ASObjectGenerator<T> {
    public BookmarkObjectGenerator() {
      super((Class<? extends T>) BookmarkObject.class);
    }
    public BookmarkObjectGenerator(Class<T> _class) {
      super(_class);
    }
    public <X extends BookmarkObjectGenerator<T>>X targetUrl(String uri) {
      item.setTargetUrl(uri);
      return (X)this;
    }
    public <X extends BookmarkObjectGenerator<T>>X targetUrl(IRI uri) {
      item.setTargetUrl(uri != null ? uri.toString() : null);
      return (X)this;
    }
  }
}
