package tools.data.input;

import java.io.IOException;
import java.io.RandomAccessFile;

import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;

/**
 * Provides an abstract layer to a byte stream. This layer can be accessed
 * randomly.
 */
public class RandomAccessByteStream implements SeekableInputStreamBytestream {
   private RandomAccessFile raf;
   private long read = 0;

   public RandomAccessByteStream(RandomAccessFile raf) {
      super();
      this.raf = raf;
   }

   @Override
   public int readByte() {
      int temp;
      try {
         temp = raf.read();
         if (temp == -1) {
            throw new RuntimeException("EOF");
         }
         read++;
         return temp;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void seek(long offset) throws IOException {
      raf.seek(offset);
   }

   @Override
   public long getPosition() throws IOException {
      return raf.getFilePointer();
   }

   @Override
   public long getBytesRead() {
      return read;
   }

   @Override
   public long available() {
      try {
         return raf.length() - raf.getFilePointer();
      } catch (IOException e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, e);
         return 0;
      }
   }
}
