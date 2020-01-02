package provider;

import java.io.File;
import java.io.IOException;

import provider.wz.WZFile;
import provider.wz.XMLWZFile;

public class MapleDataProviderFactory {
   private final static String wzPath = System.getProperty("wzpath");

   private static MapleDataProvider getWZ(File in, boolean provideImages) {
      if (in.getName().toLowerCase().endsWith("wz") && !in.isDirectory()) {
         try {
            return new WZFile(in, provideImages);
         } catch (IOException e) {
            throw new RuntimeException("Loading WZ File failed", e);
         }
      } else {
         return new XMLWZFile(in);
      }
   }

   public static MapleDataProvider getDataProvider(File in) {
      return getWZ(in, false);
   }

   public static MapleDataProvider getImageProvidingDataProvider(File in) {
      return getWZ(in, true);
   }

   public static File fileInWZPath(String filename) {
      return new File(wzPath, filename);
   }
}