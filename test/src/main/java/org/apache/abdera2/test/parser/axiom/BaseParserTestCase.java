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
package org.apache.abdera2.test.parser.axiom;

import java.io.InputStream;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.Parser;

public abstract class BaseParserTestCase {

    private static Abdera abdera = Abdera.getInstance();

    protected static Parser getParser() {
        return abdera.getParser();
    }

    protected static <T extends Element> Document<T> parse(IRI uri) {
        try {
            String uriStr = uri.toString();
            String path = uriStr.substring(uriStr.indexOf("//") + 1);
            InputStream stream = BaseParserTestCase.class.getResourceAsStream(path);
            return getParser().parse(stream, uri.toString());
        } catch (Exception e) {
            // when getting it local fails, fall back to getting it from the server
            try {
                return getParser().parse(uri.toURL().openStream(), uri.toString());
            } catch (Exception ex) {
            }
        }
        return null;
    }

}
