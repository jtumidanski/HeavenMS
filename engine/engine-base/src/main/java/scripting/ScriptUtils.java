package scripting;

import java.util.Arrays;
import java.util.function.Function;

public class ScriptUtils {
   public static int[] pushItemIfTrue(int[] array, int item, Function<Integer, Boolean> predicate) {
      if (predicate.apply(item)) {
         int[] newArray = new int[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = item;
         return newArray;
      } else {
         return array;
      }
   }

   public static int[] pushItemsIfTrue(int[] array, int[] items, Function<Integer, Boolean> predicate) {
      int[] toAdd = Arrays.stream(items).filter(predicate::apply).toArray();
      if (toAdd.length == 0) {
         return array;
      }

      int[] newArray = new int[array.length + toAdd.length];
      System.arraycopy(array, 0, newArray, 0, array.length);
      System.arraycopy(toAdd, 0, newArray, array.length, toAdd.length);
      return newArray;
   }
}
