package org.apache.abdera2.activities.model.objects;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.ASObject.ASObjectBuilder;

public final class Objects {

  private Objects() {}
  
  /**
   * Special AS Object that represents the authenticated user
   */
  
  public static final ASObject SELF = SELF().get();
  public static final ASObject ME = ME().get();
  public static final ASObject FRIENDS = FRIENDS().get();
  public static final ASObject NETWORK = NETWORK().get();
  public static final ASObject ALL = ALL().get();
  public static final ASObject PUBLIC = PUBLIC().get();
  
  public static ASObjectBuilder SELF() {
    return ASObject.makeObject("@self");
  }
  
  /**
   * Special AS Object that represents the authenticated user.
   * synonymous with @self
   */
  public static ASObjectBuilder ME() {
    return ASObject.makeObject("@me");
  }
  
  /**
   * Special AS Object that represents the authenticated users 
   * collection of direct contacts
   */
  public static ASObjectBuilder FRIENDS() {
    return ASObject.makeObject("@friends");
  }
  
  /**
   * Special AS Object that represents a subset of the authenticated users
   * collection of direct contacts
   */
  public static ASObjectBuilder FRIENDS(String id) {
    return ASObject.makeObject("@friends").id(id);
  }
  
  /**
   * Special AS Object that represents the authenticated users collection
   * of extended contacts (e.g. friends of friends)
   */
  public static ASObjectBuilder NETWORK() {
    return ASObject.makeObject("@network");
  }
  
  /**
   * Special AS Object that represents everyone. synonymous with @public
   */
  public static ASObjectBuilder ALL() {
    return ASObject.makeObject("@all");
  }
  
  /**
   * Special AS Object that represents everyone
   */
  public static ASObjectBuilder PUBLIC() {
    return ASObject.makeObject("@public");
  }
  
  /**
   * Create an anonymous AS Object (no objectType property)
   */
  public static ASObjectBuilder anonymousObject(String id) {
    return ASObject.makeObject().id(id);
  }
  
  public static final ASObject DISCONTINUED = DISCONTINUED().get();
  public static final ASObject INSTOCK = INSTOCK().get();
  public static final ASObject INSTOREONLY = INSTOREONLY().get();
  public static final ASObject ONLINEONLY = ONLINEONLY().get();
  public static final ASObject OUTOFSTOCK = OUTOFSTOCK().get();
  public static final ASObject PREORDER = PREORDER().get();
  public static final ASObject EBOOK = EBOOK().get();
  public static final ASObject HARDCOVER = HARDCOVER().get();
  public static final ASObject PAPERBACK = PAPERBACK().get();
  public static final ASObject DAMAGED = DAMAGED().get();
  public static final ASObject NEW = NEW().get();
  public static final ASObject REFURBISHED = REFURBISHED().get();
  public static final ASObject USED = USED().get();
  
  public static ASObjectBuilder DISCONTINUED() {
    return anonymousObject("discontinued");
  }
  
  public static ASObjectBuilder INSTOCK() {
    return anonymousObject("in-stock");
  }
  
  public static ASObjectBuilder INSTOREONLY() {
    return anonymousObject("in-store-only");
  }
  
  public static ASObjectBuilder ONLINEONLY() {
    return anonymousObject("online-only");
  }
  
  public static ASObjectBuilder OUTOFSTOCK() {
    return anonymousObject("out-of-stock");
  }
  
  public static ASObjectBuilder PREORDER() {
    return anonymousObject("pre-order");
  }
  
  public static ASObjectBuilder EBOOK() {
    return anonymousObject("ebook");
  }
  
  public static ASObjectBuilder HARDCOVER() {
    return anonymousObject("hardcover");
  }
  
  public static ASObjectBuilder PAPERBACK() {
    return anonymousObject("paperback");
  }
  
  public static ASObjectBuilder DAMAGED() {
    return anonymousObject("damaged");
  }
  
  public static ASObjectBuilder NEW() {
    return anonymousObject("new");
  }
  
  public static ASObjectBuilder REFURBISHED() {
    return anonymousObject("refurbished");
  }
  
  public static ASObjectBuilder USED() {
    return anonymousObject("used");
  }
  
}