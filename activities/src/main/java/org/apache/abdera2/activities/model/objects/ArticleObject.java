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

@Name("article")
public class ArticleObject 
  extends ASObject {

  private static final long serialVersionUID = -9127545085992655433L;
  
  public ArticleObject() {}
  
  public ArticleObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public static <T extends ArticleObject>ArticleObjectGenerator<T> makeArticle() {
    return new ArticleObjectGenerator<T>();
  }
  
  public static class ArticleObjectGenerator<T extends ArticleObject> extends ASObjectGenerator<T> {
    @SuppressWarnings("unchecked")
    public ArticleObjectGenerator() {
      super((Class<? extends T>) ArticleObject.class);
    }
    public ArticleObjectGenerator(Class<? extends T> _class) {
      super(_class);
    }
  }
}
