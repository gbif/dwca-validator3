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

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class UrlUtilsTest {


  @Test
  public void testWhitespace() throws Exception {
    assertEquals("http://ecat-dev.gbif.org/repository/zoological%20names.zip",
      UrlUtils.encodeURLWhitespace("http://ecat-dev.gbif.org/repository/zoological names.zip"));
    assertEquals("http://ecat-dev.gbif.org/repository/zoological%20names.zip",
      UrlUtils.encodeURLWhitespace("http://ecat-dev.gbif.org/repository/zoological%20names.zip"));
    assertEquals("http://ecat-dev.gbif.org/repository/zoological%20names.do?arg=hello%20kitty", UrlUtils
      .encodeURLWhitespace("http://ecat-dev.gbif.org/repository/zoological names.do?arg=hello kitty  "));

    // this is the reason to have this method
    boolean syntaxException;
    try {
      URI url = new URI("http://ecat-dev.gbif.org/repository/zoological names.zip");
      syntaxException = false;
    } catch (URISyntaxException e) {
      syntaxException = true;
    }
    assertTrue(syntaxException);

    try {
      URI url = new URI(
        UrlUtils.encodeURLWhitespace("http://ecat-dev.gbif.org/repository/zoological names.zip"));
      syntaxException = false;
    } catch (URISyntaxException e) {
      syntaxException = true;
    }
    assertFalse(syntaxException);
  }
}
