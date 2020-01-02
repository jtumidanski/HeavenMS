package tools.data.output;

import java.io.ByteArrayOutputStream;

/**
 * Uses a byte array to output a stream of bytes.
 */
class BAOSByteOutputStream implements ByteOutputStream {
   private ByteArrayOutputStream byteArrayOutputStream;

   /**
    * Class constructor - Wraps the stream around a Java ByteArrayOutputStream.
    *
    * @param byteArrayOutputStream <code>The ByteArrayOutputStream</code> to wrap this around.
    */
   BAOSByteOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
      super();
      this.byteArrayOutputStream = byteArrayOutputStream;
   }

   /**
    * Writes a byte to the stream.
    *
    * @param b The byte to write to the stream.
    * @see tools.data.output.ByteOutputStream#writeByte(byte)
    */
   @Override
   public void writeByte(byte b) {
      byteArrayOutputStream.write(b);
   }
}
