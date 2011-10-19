package org.apache.abdera2.common.selector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.abdera2.common.misc.ExceptionHelper;

import com.google.common.base.Predicate;
import static com.google.common.base.Preconditions.*;

public final class PropertySelector<X> 
  extends AbstractSelector<X>
  implements Selector<X> {

  private final Method method;
  private final Predicate<? super Object> predicate;
  private final boolean matchnull;
  
  PropertySelector(Method method, Predicate<? super Object> predicate, boolean matchnull) {
    this.method = method;
    this.predicate = predicate;
    this.matchnull = matchnull;
    checkNotNull(method);
    checkNotNull(predicate);
    checkArgument(this.method.getParameterTypes().length == 0, "Checked property must have no arguments!");
  }
  
  public boolean select(Object item) {
    try {
      Object obj = method.invoke(item);
      if (obj != null)
        return predicate.apply(obj);
      else return matchnull;
    } catch (Throwable e) {
      throw ExceptionHelper.propogate(e);
    } 
  }

  @SuppressWarnings({ "rawtypes" })
  public static <X>PropertySelector<X> create(Class<X> _class, String method, Predicate predicate) {
    return create(_class,method,predicate,false);
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <X>PropertySelector<X> create(Class<X> _class, String method, Predicate predicate, boolean matchnull) {
    try {
      Method m = _class.getMethod(method);
      int mods = m.getModifiers();
      checkArgument(!Modifier.isPrivate(mods),"Checked property must not be private!");
      checkArgument(!Modifier.isStatic(mods), "Checked property must not be static!");
      return new PropertySelector(m, predicate, matchnull);
    } catch (Throwable e) {
      throw ExceptionHelper.propogate(e);
    } 
  }
}
