package tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;

public class AutoJCE {
   public static byte removeCryptographyRestrictions() {
      if (!isRestrictedCryptography()) {
         return 0;
      }
      try {
         final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
         final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
         final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");
         final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");// was set to final in Java 8 Update 112. Requires you to remove the final modifier.
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);
         modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
         isRestrictedField.setAccessible(true);
         isRestrictedField.set(null, false);
         final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
         defaultPolicyField.setAccessible(true);
         final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);
         final Field perms = cryptoPermissions.getDeclaredField("perms");
         perms.setAccessible(true);
         ((Map<?, ?>) perms.get(defaultPolicy)).clear();
         final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
         instance.setAccessible(true);
         defaultPolicy.add((Permission) instance.get(null));
         return 1;
      } catch (final Exception e) {
         e.printStackTrace();

         System.err.println("Failed to remove cryptography restrictions");
         return -1;
      }
   }

   private static boolean isRestrictedCryptography() {
      // This simply matches the Oracle JRE, but not OpenJDK.
      return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
   }
}