package tools.data.output;

import java.io.ByteArrayOutputStream;

import tools.HexTool;

public class MaplePacketLittleEndianWriter extends GenericLittleEndianWriter {
   public static final int DEFAULT_SIZE = 32;

   private ByteArrayOutputStream byteArrayOutputStream;

   /**
    * Constructor - initializes this stream with a default size.
    */
   public MaplePacketLittleEndianWriter() {
      this(DEFAULT_SIZE);
   }

   /**
    * Constructor - initializes this stream with size <code>size</code>.
    *
    * @param size The size of the underlying stream.
    */
   public MaplePacketLittleEndianWriter(int size) {
      this.byteArrayOutputStream = new ByteArrayOutputStream(size);
      setByteOutputStream(new BAOSByteOutputStream(byteArrayOutputStream));
   }

   /**
    * Gets a <code>MaplePacket</code> instance representing this
    * sequence of bytes.
    *
    * @return A <code>MaplePacket</code> with the bytes in this stream.
    */
   public byte[] getPacket() {
      return byteArrayOutputStream.toByteArray();
   }

   /**
    * Changes this packet into a human-readable hexadecimal stream of bytes.
    *
    * @return This packet as hex digits.
    */
   @Override
   public String toString() {
      return HexTool.toString(byteArrayOutputStream.toByteArray());
   }
}
