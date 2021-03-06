package com.gemstone.gemfire.management.internal.configuration.utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.gemstone.gemfire.internal.cache.xmlcache.CacheXml;

/******
 * This class is used to resolve the location of DTD. 
 * During development time the dtd for the latest version is not published 
 * on the www.gemstone.com , then the dtd should be picked up as a resource from the Jar file.
 * @author bansods
 *
 * @deprecated As of 8.1 use {@link CacheXml}
 */
@Deprecated
public class DtdResolver implements EntityResolver{

  @Deprecated
  public InputSource resolveEntity (String publicId, String systemId) throws IOException
  {
    if (!isHttpUrlOK(systemId)) {
      URL dtdURL = getClass().getResource(CacheXml.LATEST_DTD_LOCATION);
      File dtd = new File("gemfire.dtd");
      FileUtils.copyURLToFile(dtdURL, dtd);
      InputSource inputSource = new InputSource(FileUtils.openInputStream(dtd));
      FileUtils.deleteQuietly(dtd);
      return inputSource;
    } else {
      return null;
    }
  }
  /****
   * Checks if the url passed , can be contacted or not.
   * @param urlString
   * @return true if the URL is up and can be contacted.
   */
  @Deprecated
  public boolean isHttpUrlOK(String urlString) {
    try {
      URL e = new URL(urlString);
      HttpURLConnection urlConnection = (HttpURLConnection) e.openConnection();
      urlConnection.setRequestMethod("HEAD");
      int responseCode = urlConnection.getResponseCode();
      if (responseCode == 200) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }  
  }
 
  /***
   * Gets the URL for Cache dtd
   * @return dtd url as string
   * @throws MalformedURLException
   */
  @Deprecated
  public URL getDtdUrl() throws MalformedURLException {
    if (isHttpUrlOK(CacheXml.LATEST_SYSTEM_ID)) {
      return new URL(CacheXml.LATEST_SYSTEM_ID);
    } else {
      URL dtdURL = getClass().getResource(CacheXml.LATEST_DTD_LOCATION);
      return dtdURL;
    }
  }
}
