package tools.data.input;

/**
 * Represents an abstract stream of bytes.
 */
public interface ByteInputStream {
   int readByte();

   long getBytesRead();

   long available();
}
