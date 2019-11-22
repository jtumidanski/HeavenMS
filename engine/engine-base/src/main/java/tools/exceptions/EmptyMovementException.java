package tools.exceptions;

import tools.data.input.LittleEndianAccessor;

/**
 * @author Ronan
 */
public class EmptyMovementException extends Exception {

   public EmptyMovementException(LittleEndianAccessor lea) {
      super("Empty movement: " + lea);
   }

}