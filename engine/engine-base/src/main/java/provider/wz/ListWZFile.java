package provider.wz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import provider.MapleDataProviderFactory;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.InputStreamByteStream;
import tools.data.input.LittleEndianAccessor;

public class ListWZFile {
   private static Collection<String> modernImages = new HashSet<>();
   private LittleEndianAccessor accessor;
   private List<String> entries = new ArrayList<>();

   public ListWZFile(File listWz) throws FileNotFoundException {
      accessor = new GenericLittleEndianAccessor(new InputStreamByteStream(new BufferedInputStream(new FileInputStream(listWz))));
      while (accessor.available() > 0) {
         int l = accessor.readInt() * 2;
         byte[] chunk = new byte[l];
         for (int i = 0; i < chunk.length; i++) {
            chunk[i] = accessor.readByte();
         }
         accessor.readChar();
         final String value = String.valueOf(WZTool.readListString(chunk));
         entries.add(value);
      }
      entries = Collections.unmodifiableList(entries);
   }

   public static byte[] xorBytes(byte[] a, byte[] b) {
      byte[] wusched = new byte[a.length];
      for (int i = 0; i < a.length; i++) {
         wusched[i] = (byte) (a[i] ^ b[i]);
      }
      return wusched;
   }

   public static void init() {
      final String listWz = System.getProperty("listwz");
      if (listWz != null) {
         ListWZFile listwz;
         try {
            listwz = new ListWZFile(MapleDataProviderFactory.fileInWZPath("List.wz"));
            modernImages = new HashSet<>(listwz.getEntries());
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
      }
   }

   public static boolean isModernImgFile(String path) {
      return modernImages.contains(path);
   }

   public List<String> getEntries() {
      return entries;
   }
}
