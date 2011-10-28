package org.apache.abdera2.util;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.protocol.ProviderHelper;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.ExtensibleElement;

import com.google.common.base.Predicate;

public class MorePredicates {

  public static final Predicate<IRI> VALID_ATOM_ID = 
    new Predicate<IRI>() {
      public boolean apply(IRI input) {
        return input != null && 
               input.toString().trim().length() > 0 &&
               input.isAbsolute();
      }
  };
  
  private static boolean is_media(Content content) {
    return content.getSrc() != null || 
           content.getContentType() == Content.Type.MEDIA;
  }
  
  /**
   * Check to see if the entry is minimally valid according to RFC4287. This is not a complete check. It just verifies
   * that the appropriate elements are present and that their values can be accessed.
   */
  public static final Predicate<Entry> VALID_ENTRY = 
    new Predicate<Entry>() {
      public boolean apply(Entry entry) {
        try {
          if (!VALID_ATOM_ID.apply(entry.getId()))
              return false;
          if (entry.getTitle() == null)
              return false;
          if (entry.getUpdated() == null)
              return false;
          if (entry.getAuthorInherited() == null)
              return false;
          Content content = entry.getContentElement();
          if (content == null)
            if (entry.getAlternateLink() == null)
              return false;
          else
            if (is_media(content) && !entry.has(Constants.SUMMARY))
              return false;
        } catch (Exception e) {
          ProviderHelper.log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", false));
          return false;
        }
        ProviderHelper.log.debug(Localizer.sprintf("CHECKING.VALID.ENTRY", true));
        return true;
      }
  }; 
  
  public static Predicate<Element> checkElementNamespaces(
    final Set<String> ignore) {
    return new Predicate<Element>() {
      public boolean apply(Element element) {
        List<QName> attrs = 
          element.getExtensionAttributes();
        for (QName qname : attrs)
          if (!ignore.contains(qname.getNamespaceURI()))
            return false;
        if (element instanceof ExtensibleElement) {
          ExtensibleElement ext = (ExtensibleElement)element;
          for (Element el : ext.getExtensions()) {
            QName qname = el.getQName();
            String ns = qname.getNamespaceURI();
            if (!ignore.contains(ns))
              return false;
            if (!apply(el))
              return false;
          }
        }
        return true;
      }
    };
  }
}
