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
package org.apache.abdera2.common.protocol;


/**
 * Metadata interface used by WorkspaceManager and Provider implementations to construct Atompub Service Documents. The
 * CollectionInfo interface provides information used to construct an app:collection element
 */
public interface CollectionInfo {

    /**
     * Get the value of the app:collection element's href attribute. This must not be null
     */
    String getHref(RequestContext request);

    /**
     * Get the value of the app:collection element's atom:title element. This assumes that the title will be
     * type="text". This must not be null;
     */
    String getTitle(RequestContext request);

    /**
     * Returns an array of MIME media types for the app:collection element's app:accept elements. These tell a client
     * which media types the collection will accept on a POST
     */
    Iterable<String> getAccepts(RequestContext request);

}
