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
package org.apache.abdera2.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.abdera2.common.misc.MoreFunctions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Iterators;

@SuppressWarnings("unchecked")
public final class Discover {

    private final static Log log = LogFactory.getLog(Discover.class);
    private Discover() {}

    public static <T> T locate(
      Class<T> _class, 
      @Nullable String defaultImpl, 
      Object... args) {
        return (T)locate(
          _class, 
          defaultImpl, 
          getLoader(), 
          args);
    }
    
    public static <T> T locate(
      String id, 
      @Nullable String defaultImpl, 
      Object... args) {
        return (T)locate(
          id, 
          defaultImpl, 
          getLoader(), 
          args);
    }

    public static <T> T locate(
      Class<T> _class, 
      @Nullable String defaultImpl, 
      ClassLoader loader, 
      Object... args) {
      try {
        T instance = null, first = null;
        Iterable<T> items = 
          locate(_class, loader, args);
        Iterator<T> is = items.iterator();
        if (defaultImpl == null)
          defaultImpl = AnnoUtil.getDefaultImplementation(_class);
        while (instance == null && is.hasNext()) {
          T i = is.next();
          if (defaultImpl != null && defaultImpl.equals(i.getClass().getName())) {
            instance = i; 
            break;
          } else if (first == null)
            first = i;
        }
        instance = instance != null ? instance : first;
        return instance != null ? 
          instance :
          (T)load(loader, defaultImpl, false, args);
      } catch (Throwable t) {
          throw new RuntimeException(t);
      }      
    }
    
    public static <T> T locate(
      String id, 
      @Nullable String defaultImpl, 
      ClassLoader loader, 
      Object... args) {
        try {
            T instance = null;
            Iterable<T> items = 
              locate(id, loader, args);
            for (T i : items) {
                instance = i;
                break;
            }
            return instance != null ? 
              instance :
              (T)load(loader, defaultImpl, false, args);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static ClassLoader getLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static <T> Iterable<T> locate(
      Class<T> _class, 
      @Nullable ClassLoader cl, 
      Object... args) {
        return locate(_class, false, cl, args);
    }
    
    public static <T> Iterable<T> locate(
      String id, 
      @Nullable ClassLoader cl, 
      Object... args) {
        return locate(id, false, cl, args);
    }

    public static <T> Iterable<T> locate(
      Class<T> _class, 
      boolean classesonly, 
      @Nullable ClassLoader cl, 
      Object... args) {
      return locate(
        _class, 
        classesonly, 
        new DefaultLoader<T>(
          _class, 
          classesonly, 
          args, 
          cl));
    }
    
    public static <T> Iterable<T> locate(
      String id, 
      boolean classesonly, 
      @Nullable ClassLoader cl, 
      Object... args) {
        return locate(
          id, 
          classesonly, 
          new DefaultLoader<T>(
            id, 
            classesonly, 
            args, 
            cl));
    }

    public static <T> Iterable<T> locate(
      Class<T> _class, 
      Object...args) {
      return locate(
        _class, 
        false, 
        args);
    }
    
    public static <T> Iterable<T> locate(
      String id, 
      Object... args) {
        return locate(id, false, args);
    }

    public static <T> Iterable<T> locate(
      Class<T> _class, 
      boolean classesonly, 
      Object... args) {
        return locate(
          new DefaultLoader<T>(
            _class, 
            classesonly, 
            args));
    }
    
    public static <T> Iterable<T> locate(
      String id, 
      boolean classesonly, 
      Object... args) {
        return locate(
          new DefaultLoader<T>(
            id, 
            classesonly, 
            args));
    }

    public static <T> Iterable<T> locate(
      Iterable<T> loader) {
        Set<T> impls = new LinkedHashSet<T>();
        try {
          for (T instance : loader)
            if (instance != null)
              impls.add(instance);
        } catch (Throwable t) {
          log.error(t);
        }
        return impls;
    }

    public static class DefaultLoader<T> 
      implements Iterable<T> {
        protected final ClassLoader loader;
        protected final String id;
        protected final Iterator<T> iterator;
        protected final Object[] args;

        public DefaultLoader(
          String id, 
          boolean classesonly, 
          Object[] args) {
            this(
              id, 
              classesonly, 
              args, 
              getLoader());
        }

        public DefaultLoader(
          String id, 
          boolean classesonly, 
          Object[] args, 
          @Nullable ClassLoader loader) {
            this.loader = 
              loader != null ? 
                loader : getLoader();
            this.id = id;
            this.args = args;
            this.iterator = init(classesonly);
        }

        public DefaultLoader(
          Class<T> _class, 
          boolean classesonly, 
          Object[] args, 
          @Nullable ClassLoader loader) {
            this(
              _class.getName(), 
              classesonly, 
              args, 
              loader);
        }
        
        public DefaultLoader(
          Class<T> _class, 
          boolean classesonly, 
          Object[] args) {
            this(
              _class.getName(), 
              classesonly, 
              args);
        }
        
        private Iterator<T> init(boolean classesonly) {
          try {
            Set<Iterator<T>> list = new HashSet<Iterator<T>>();
            Enumeration<URL> e = locateResources(
              "META-INF/services/" + id, //$NON-NLS-1$ 
              loader,
              Discover.class);
              while (e.hasMoreElements()) {
                Iterator<T> i =
                  new DefaultLoaderIterator<T>(
                    loader, 
                    e.nextElement().openStream(), 
                    classesonly, 
                    args);
                list.add(i);
              }
            return Iterators.concat(list.iterator());
          } catch (Throwable t) {
            throw ExceptionHelper.propogate(t);
          }
        }
        public Iterator<T> iterator() {
          return iterator;
        }
    }

    public static class DefaultLoaderIterator<T> 
      extends LineReaderLoaderIterator<T> {
      public DefaultLoaderIterator(
        ClassLoader cl, 
        InputStream in, 
        boolean classesonly, 
        Object[] args) {
          super(cl, in, classesonly, args);
      }

      public T next() {
        try {
          if (!hasNext())
            return null;
          return create(read(), args);
        } catch (Throwable t) {
           return null;
        }
      }

      protected T create(
        String spec, 
        Object[] args) {
          try {
            return Discover.<T>load(cl, spec, classesonly, args);
          } catch (Throwable t) {
            throw ExceptionHelper.propogate(t);
          }
      }
    }

    static <T> T load(
      ClassLoader loader,
      String spec, 
      boolean classesonly, 
      Object[] args) 
        throws Exception {
        if (classesonly)
          return (T)getClass(loader, spec);
        else {
          Class<T> _class = 
            getClass(loader, spec);
          return MoreFunctions
            .<T>createInstance(_class)
            .apply(args);
        }
    }

    private static <T> Class<T> getClass(
      ClassLoader loader, 
      String spec) {
        Class<T> c = null;
        try {
          c = (Class<T>)loader.loadClass(spec);
        } catch (ClassNotFoundException e) {
          try {
            // try loading the class from the Discover class loader
            // if the loader failed.
            c = (Class<T>)Discover.class.getClassLoader().loadClass(spec);
          } catch (ClassNotFoundException e1) {
            // throw the original exception
            throw new RuntimeException(e);
          }
        }
        return c;
    }

    public static abstract class LineReaderLoaderIterator<T> 
      extends LoaderIterator<T> {
        private BufferedReader buf = null;
        private String line = null;
        protected final Object[] args;
        protected final boolean classesonly;

        protected LineReaderLoaderIterator(
          ClassLoader cl, 
          InputStream in, 
          boolean classesonly, 
          Object[] args) {
            super(cl);
            this.args = args;
            this.classesonly = classesonly;
            try {
              InputStreamReader reader = 
                new InputStreamReader(in, "UTF-8");
              buf = new BufferedReader(reader);
              line = readNext();
            } catch (Throwable t) {
              throw ExceptionHelper.propogate(t);
            }
        }

        public boolean hasNext() {
          return line != null;
        }

        protected String readNext() {
          try {
            String line = null;
            while ((line = buf.readLine()) != null) {
              line = line.trim();
              if (!line.startsWith("#"))break; //$NON-NLS-1$
            }
            return line;
          } catch (Throwable t) {
            throw new RuntimeException(t);
          }
        }

        protected String read() {
          String val = line;
          line = readNext();
          return val;
        }
    }

    public static abstract class LoaderIterator<T> 
      implements Iterator<T> {
        protected final ClassLoader cl;
        protected LoaderIterator(ClassLoader cl) {
          this.cl = cl;
        }
        public void remove() {}
    }

    public static URL locateResource(
      String id, 
      ClassLoader loader, 
      Class<?> callingClass) {
        URL url = loader.getResource(id);
        if (url == null && id.startsWith("/"))
          url = loader.getResource(id.substring(1));
        if (url == null)
          url = locateResource(id, Discover.class.getClassLoader(), callingClass);
        if (url == null && callingClass != null)
          url = locateResource(id, callingClass.getClassLoader(), null);
        if (url == null)
          url = callingClass.getResource(id);
        if ((url == null) && id.startsWith("/"))
          url = callingClass.getResource(id.substring(1));
        return url;
    }

    public static Enumeration<URL> locateResources(
      String id, 
      ClassLoader loader, 
      Class<?> callingClass)
        throws IOException {
      Enumeration<URL> urls = loader.getResources(id);
      if (urls == null && id.startsWith("/"))
        urls = loader.getResources(id.substring(1));
      if (urls == null)
        urls = locateResources(id, Discover.class.getClassLoader(), callingClass);
      if (urls == null)
        urls = locateResources(id, callingClass.getClassLoader(), callingClass);
      return urls;
    }

    public static InputStream locateResourceAsStream(
      String resourceName, 
      ClassLoader loader, 
      Class<?> callingClass) {
        URL url = locateResource(resourceName, loader, callingClass);
        try {
          return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
          return null;
        }
    }
}
