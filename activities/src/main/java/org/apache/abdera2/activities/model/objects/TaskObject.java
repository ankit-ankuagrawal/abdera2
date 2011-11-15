package org.apache.abdera2.activities.model.objects;

import java.util.Map;

import org.apache.abdera2.activities.io.gson.Properties;
import org.apache.abdera2.activities.io.gson.Property;
import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.date.DateTimes;
import org.apache.abdera2.common.iri.IRI;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.google.common.collect.ImmutableSet;

/**
 * Special object type intended to describe the actions that can be 
 * taken in response to another object.
 * 
 * this is purely experimental at this point and is something 
 * that I'm kicking around with the AS community. It's included 
 * here as a basic proof of the concept and to get some implementation
 * experience playing around with it
 */
public class TaskObject extends ASObject {

  public TaskObject(Map<String,Object> map) {
    super(map,TaskBuilder.class,TaskObject.class);
  }
  
  public <X extends TaskObject, M extends Builder<X,M>>TaskObject(Map<String,Object> map, Class<M> _class, Class<X> _obj) {
    super(map,_class,_obj);
  }
  
  public <X extends ASObject>X getObject() {
    return getProperty("for");
  }
  
  public <X extends ASObject>X getActor() {
    return getProperty("actor");
  }
  
  public Iterable<String> getSupersedes() {
    return checkEmpty(this.<Iterable<String>>getProperty("supersedes"));
  }
  
  public boolean isRequired() {
    return (Boolean)getProperty("required");
  }
  
  public DateTime getBy() {
    return getProperty("by");
  }

  public Verb getVerb() {
    return getProperty("verb");
  }
  
  public IRI getResourceLink() {
    return getProperty("resourceLink");
  }
  
  public static TaskBuilder makeTask() {
    return new TaskBuilder("task");
  }
 
  @Name("task")
  @Properties({
    @Property(name="by",to=DateTime.class),
    @Property(name="supersedes",to=String.class),
    @Property(name="resourceLink",to=IRI.class),
    @Property(name="for",to=ASObject.class)
  })
  public static final class TaskBuilder extends Builder<TaskObject,TaskBuilder> {
    public TaskBuilder() {
      super(TaskObject.class,TaskBuilder.class);
    }
    public TaskBuilder(Map<String, Object> map) {
      super(map, TaskObject.class,TaskBuilder.class);
    }
    public TaskBuilder(String objectType) {
      super(objectType, TaskObject.class,TaskBuilder.class);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static abstract class Builder<X extends TaskObject,M extends Builder<X,M>> 
    extends ASObject.Builder<X,M> {
    
    public Builder(Class<X>_class,Class<M>_builder) {
      super(_class,_builder);
    }
    public Builder(String objectType,Class<X>_class,Class<M>_builder) {
      super(objectType,_class,_builder);
    }
    public Builder(Map<String,Object> map,Class<X>_class,Class<M>_builder) {
      super(map,_class,_builder);
    }
    public M object(ASObject object) {
      set("for",object);
      return (M)this;
    }
    public M actor(ASObject object) {
      set("actor",object);
      return (M)this;
    }
    public M supersedes(String... ids) {
      if (ids == null) return (M)this;
      set("supersedes",ImmutableSet.copyOf(ids));
      return (M)this;
    }
    public M required() {
      set("required",true);
      return (M)this;
    }
    public M by(DateTime dateTime) {
      set("by",dateTime);
      return (M)this;
    }
    public M byNow() {
      set("by",DateTimes.now());
      return (M)this;
    }
    public M by(Duration duration) {
      return by(DateTimes.now().plus(duration));
    }
    public M selfLink(IRI iri) {
      set("selfLink", iri);
      return (M)this;
    }
    public M selfLink(String iri) {
      return selfLink(new IRI(iri));
    }
    public M verb(Verb verb) {
      set("verb",verb);
      return (M)this;
    }
    public M resourceLink(IRI iri) {
      set("resourceLink",iri);
      return (M)this;
    }
    public M resourceLink(String iri) {
      return resourceLink(new IRI(iri));
    }
  }
  
}
