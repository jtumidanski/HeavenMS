package tools;

import java.io.ByteArrayOutputStream;

import constants.string.CharsetConstants;

public class HexTool {
   private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   private static String toString(byte byteValue) {
      int tmp = byteValue << 8;
      char[] retstr = new char[]{HEX[(tmp >> 12) & 0x0F], HEX[(tmp >> 8) & 0x0F]};
      return String.valueOf(retstr);
   }

   public static String toString(byte[] bytes) {
      StringBuilder hexed = new StringBuilder();
      for (byte aByte : bytes) {
         hexed.append(toString(aByte));
         hexed.append(' ');
      }
      return hexed.substring(0, hexed.length() - 1);
   }

   public static String toCompressedString(byte[] bytes) {
      StringBuilder hexed = new StringBuilder();
      for (byte aByte : bytes) {
         hexed.append(toString(aByte));
      }
      return hexed.substring(0, hexed.length());
   }

   public static byte[] getByteArrayFromHexString(String hex) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int nexti = 0;
      int nextb = 0;
      boolean highoc = true;
      outer:
      for (; ; ) {
         int number = -1;
         while (number == -1) {
            if (nexti == hex.length()) {
               break outer;
            }
            char chr = hex.charAt(nexti);
            if (chr >= '0' && chr <= '9') {
               number = chr - '0';
            } else if (chr >= 'a' && chr <= 'f') {
               number = chr - 'a' + 10;
            } else if (chr >= 'A' && chr <= 'F') {
               number = chr - 'A' + 10;
            } else {
               number = -1;
            }
            nexti++;
         }
         if (highoc) {
            nextb = number << 4;
            highoc = false;
         } else {
            nextb |= number;
            highoc = true;
            baos.write(nextb);
         }
      }
      return baos.toByteArray();
   }

   public static String toStringFromAscii(final byte[] bytes) {
      byte[] ret = new byte[bytes.length];
      for (int x = 0; x < bytes.length; x++) {
         if (bytes[x] < 32 && bytes[x] >= 0) {
            ret[x] = '.';
         } else {
            int chr = ((short) bytes[x]) & 0xFF;
            ret[x] = (byte) chr;
         }
      }
      String encode = CharsetConstants.MAPLE_TYPE.getAscii();
      try {
         return new String(ret, encode);
      } catch (Exception ignored) {
      }
      return "";
   }

}
