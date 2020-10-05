package tools.data.input;

import java.io.IOException;

import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;

/**
 * Provides an abstract accessor to a generic Little Endian byte stream. This
 * accessor is seekable.
 */
public class GenericSeekableLittleEndianAccessor extends GenericLittleEndianAccessor implements SeekableLittleEndianAccessor {
   private SeekableInputStreamBytestream bs;

   /**
    * Class constructor
    * Provide a seekable input stream to wrap this object around.
    *
    * @param bs The byte stream to wrap this around.
    */
   public GenericSeekableLittleEndianAccessor(SeekableInputStreamBytestream bs) {
      super(bs);
      this.bs = bs;
   }

   /**
    * Seek the pointer to <code>offset</code>
    *
    * @param offset The offset to seek to.
    * @see tools.data.input.SeekableInputStreamBytestream#seek
    */
   @Override
   public void seek(long offset) {
      try {
         bs.seek(offset);
      } catch (IOException e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, e);
      }
   }

   /**
    * Get the current position of the pointer.
    *
    * @return The current position of the pointer as a long integer.
    * @see tools.data.input.SeekableInputStreamBytestream#getPosition
    */
   @Override
   public long getPosition() {
      try {
         return bs.getPosition();
      } catch (IOException e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, e);
         return -1;
      }
   }

   /**
    * Skip <code>num</code> number of bytes in the stream.
    *
    * @param num The number of bytes to skip.
    */
   @Override
   public void skip(int num) {
      seek(getPosition() + num);
   }
}
