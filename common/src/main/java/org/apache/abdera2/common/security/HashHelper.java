package org.apache.abdera2.common.security;

import java.io.InputStream;
import java.security.Key;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;

import javax.crypto.Mac;

import org.apache.abdera2.common.misc.ExceptionHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

@SuppressWarnings("unchecked")
public final class HashHelper {

  private HashHelper() {}
  
  public static Function<byte[],String> sig(
    final PrivateKey key, final String alg) {
      return new Function<byte[],String>() {
        public String apply(byte[] input) {
          return sig(key,alg,input);
        }
      };
  }
  
  public static Function<byte[],String> hmac(
    final Key key, final String alg) {
      return new Function<byte[],String>() {
        public String apply(byte[] input) {
          return hmac(key,alg,input);
        }
      };
  }
  
  public static Predicate<byte[]> signatureValid(
    final PublicKey key, 
    final String alg, 
    final byte[] source) {
    return new Predicate<byte[]>() {
      public boolean apply(byte[] mat) {
        return sigval(key,alg,source,mat);
      }
    };
  }
  
  public static Predicate<byte[]> hmacValid(
      final Key key, 
      final String alg, 
      final byte[] source) {
      return new Predicate<byte[]>() {
        public boolean apply(byte[] mat) {
          return hmacval(key,alg,source,mat);
        }
      };
    }
  
  public static String sig(PrivateKey key, String alg, byte[] mat) {
    try {
      Signature sig = Signature.getInstance(alg);
      sig.initSign((PrivateKey)key);
      sig.update(mat);
      byte[] dat = sig.sign();
      return Base64.encodeBase64URLSafeString(dat);
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  public static String hmac(Key key, String alg, byte[] mat) {
    try {
      Mac mac = Mac.getInstance(alg);
      mac.init(key);
      byte[] sig = mac.doFinal(mat);
      return Base64.encodeBase64URLSafeString(sig);
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  public static boolean sigval(PublicKey key, String alg, byte[] mat, byte[] dat) {
    try {
      Signature sig = Signature.getInstance(alg);
      sig.initVerify(key);
      sig.update(mat);
      return sig.verify(dat);
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  public static boolean hmacval(Key key, String alg, byte[] mat, byte[] dat) {
    try {
      Mac mac = Mac.getInstance(alg);
      mac.init(key);
      byte[] sig = mac.doFinal(mat);
      return Arrays.equals(sig, dat);
    } catch (Throwable t) {
      throw ExceptionHelper.propogate(t);
    }
  }
  
  public static abstract class Hasher 
    implements Supplier<String> {
    public <T extends Hasher>T update(byte[] buf) {
      update(buf,0,buf.length);
      return (T)this;
    }
    public abstract <T extends Hasher>T update(byte[] buf, int s, int e);
    protected abstract byte[] digest();
    public String get() {
      return Hex.encodeHexString(digest());
    }
    public String name() {
      return getClass().getSimpleName().toLowerCase();
    }
  }
    
  public static Function<byte[],String> md5() {
    return new Function<byte[],String>() {
      public String apply(byte[] input) {
        return new Md5().update(input).get();
      }
    };
  }
  
  public static class Md5 extends Hasher {
    private final MessageDigest md;
    public Md5() {
      this.md = init();
    }
    private MessageDigest init() {
      try {
        return MessageDigest.getInstance("MD5");
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    public <T extends Hasher>T update(byte[] buf, int s, int e) {
      if (md != null)
        md.update(buf, s, e);
      return (T)this;
    }
    public byte[] digest() {
      return md.digest();
    }
  }
  
  public abstract static class SHA extends Hasher {
    private final Mac mac;
    SHA(Key key, String alg) {
      this.mac = init(key,alg);
    }
    private Mac init(Key key, String alg) {
      try {
        Mac mac = Mac.getInstance(alg);
        mac.init(key);
        return mac;
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
    public <T extends Hasher>T update(byte[] buf, int s, int e) {
      mac.update(buf, s, e);
      return (T)this;
    }
    public byte[] digest() {
      return mac.doFinal();
    }
  }
  
  
  public static Function<byte[],String> sha256(final Key key) {
    return new Function<byte[],String>() {
      public String apply(byte[] input) {
        return new SHA256(key).update(input).get();
      }
    };
  }
  
  public static Function<byte[],String> sha384(final Key key) {
    return new Function<byte[],String>() {
      public String apply(byte[] input) {
        return new SHA384(key).update(input).get();
      }
    };
  }
  
  public static Function<byte[],String> sha512(final Key key) {
    return new Function<byte[],String>() {
      public String apply(byte[] input) {
        return new SHA512(key).update(input).get();
      }
    };
  }
  
  public static class SHA256 extends SHA {
    public SHA256(Key key) {
      super(key, "HmacSHA256");
    }
  }
  
  public static class SHA384 extends SHA {
    public SHA384(Key key) {
      super(key, "HmacSHA384");
    }
  }
  
  public static class SHA512 extends SHA {
    public SHA512(Key key) {
      super(key, "HmacSHA512");
    }
  }
  
  private static abstract class IOHasherFunction 
    implements Function<InputStream,String> {
    protected abstract Hasher hasher();
    public String apply(InputStream input) {
      try {
        Md5 md5 = new Md5();
        byte[] buf = new byte[1024];
        int r = -1;
        while((r = input.read(buf)) > -1)
          md5.update(buf, 0, r);
        return md5.get();
      } catch (Throwable t) {
        throw ExceptionHelper.propogate(t);
      }
    }
  }
  
  public static Function<InputStream,String> md5stream() {
    return new IOHasherFunction() {
      protected Hasher hasher() {
        return new Md5();
      }
    };
  }
  
  public static Function<InputStream,String> sha256stream(final Key key) {
    return new IOHasherFunction() {
      protected Hasher hasher() {
        return new SHA256(key);
      }
    };
  }
  
  public static Function<InputStream,String> sha384stream(final Key key) {
    return new IOHasherFunction() {
      protected Hasher hasher() {
        return new SHA384(key);
      }
    };
  }
  
  public static Function<InputStream,String> sha512stream(final Key key) {
    return new IOHasherFunction() {
      protected Hasher hasher() {
        return new SHA512(key);
      }
    };
  }
  
  public static String md5(InputStream in) {
    return md5stream().apply(in);
  }
  
  public static String sha256(Key key, InputStream in) {
    return sha256stream(key).apply(in);
  }
  
  public static String sha384(Key key, InputStream in) {
    return sha384stream(key).apply(in);
  }
  
  public static String sha512(Key key, InputStream in) {
    return sha512stream(key).apply(in);
  }
  
  public static String md5(byte[] in) {
    return md5().apply(in);
  }
  
  public static String sha256(Key key, byte[] in) {
    return sha256(key).apply(in);
  }
  
  public static String sha384(Key key, byte[] in) {
    return sha384(key).apply(in);
  }
  
  public static String sha512(Key key, byte[] in) {
    return sha512(key).apply(in);
  }
  
}
