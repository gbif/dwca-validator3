/*
 * Copyright 2011 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.dwca.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilitiy class for dealing with URLs in addition to the methods
 * found in commonly used classes:
 *
 * @See java.net.URL
 * @See java.net.URI
 * @See java.net.URLEncoder
 * @See org.apache.http.client.utils.URLEncodedUtils
 */
public class UrlUtils {

  // hide constructor for utility classes
  private UrlUtils(){

  }

  /*
    method to escape whitespace for complete Url strings as
    httpclient has removed the convenient org.apache.commons.httpclient.util.URIUtil class in its 4.x version

    Whitespace in a URL path is not allowed and should be replaced with %20.
    Creating a URI instance with whitespace results in a URISyntaxException

    The regular URL encoding is for parameters only, but not suited to escape the path, protocol or host part.
    If the url at hand can be atomised into these parts the URI class provides constructors that deal with escaping.

    If only a complete url string exists this method comes in handy to avoid the URISyntaxException.

   */

  public static String encodeURLWhitespace(final String s) {
    return StringUtils.trimToEmpty(s).replaceAll(" ", "%20");
  }

}
