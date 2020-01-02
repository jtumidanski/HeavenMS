package tools;

public class StringUtil {
   /**
    * Gets a string padded from the left to <code>length</code> by
    * <code>padCharacter</code>.
    *
    * @param in      The input string to be padded.
    * @param padCharacter The character to pad with.
    * @param length  The length to pad to.
    * @return The padded string.
    */
   public static String getLeftPaddedStr(String in, char padCharacter, int length) {
      return String.valueOf(padCharacter).repeat(Math.max(0, length - in.length())) + in;
   }

   /**
    * Gets a string padded from the right to <code>length</code> by
    * <code>padCharacter</code>.
    *
    * @param in      The input string to be padded.
    * @param padCharacter The character to pad with.
    * @param length  The length to pad to.
    * @return The padded string.
    */
   public static String getRightPaddedStr(String in, char padCharacter, int length) {
      return in + String.valueOf(padCharacter).repeat(Math.max(0, length - in.length()));
   }

   /**
    * Joins an array of strings starting from string <code>start</code> with
    * a space.
    *
    * @param arr   The array of strings to join.
    * @param start Starting from which string.
    * @return The joined strings.
    */
   public static String joinStringFrom(String[] arr, int start) {
      return joinStringFrom(arr, start, " ");
   }

   /**
    * Joins an array of strings starting from string <code>start</code> with
    * <code>sep</code> as a separator.
    *
    * @param arr   The array of strings to join.
    * @param start Starting from which string.
    * @return The joined strings.
    */
   public static String joinStringFrom(String[] arr, int start, String sep) {
      StringBuilder builder = new StringBuilder();
      for (int i = start; i < arr.length; i++) {
         builder.append(arr[i]);
         if (i != arr.length - 1) {
            builder.append(sep);
         }
      }
      return builder.toString();
   }

   /**
    * Makes an enum name human readable (fixes spaces, capitalization, etc)
    *
    * @param enumName The name of the enum to neaten up.
    * @return The human-readable enum name.
    */
   public static String makeEnumHumanReadable(String enumName) {
      StringBuilder builder = new StringBuilder(enumName.length() + 1);
      String[] words = enumName.split("_");
      for (String word : words) {
         if (word.length() <= 2) {
            builder.append(word); // assume that it's an abbreviation
         } else {
            builder.append(word.charAt(0));
            builder.append(word.substring(1).toLowerCase());
         }
         builder.append(' ');
      }
      return builder.substring(0, enumName.length());
   }

   /**
    * Counts the number of <code>chr</code>'s in <code>str</code>.
    *
    * @param str The string to check for instances of <code>chr</code>.
    * @param chr The character to check for.
    * @return The number of times <code>chr</code> occurs in <code>str</code>.
    */
   public static int countCharacters(String str, char chr) {
      int ret = 0;
      for (int i = 0; i < str.length(); i++) {
         if (str.charAt(i) == chr) {
            ret++;
         }
      }
      return ret;
   }

   /**
    * Replaces difficult to read character sequences with an easier to read variant.
    *
    * @param in the difficult to read string
    * @return a more readable string
    */
   public static String makeMapleReadable(String in) {
      String i = in.replace('I', 'i');
      i = i.replace('l', 'L');
      i = i.replace("rn", "Rn");
      i = i.replace("vv", "Vv");
      i = i.replace("VV", "Vv");

      return i;
   }
}