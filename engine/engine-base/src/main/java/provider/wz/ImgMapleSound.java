package provider.wz;

public class ImgMapleSound {
   private int dataLength, offset;

   public ImgMapleSound(int dataLength, int offset) {
      this.dataLength = dataLength;
      this.offset = offset;
   }

   public int getDataLength() {
      return dataLength;
   }

   public int getOffset() {
      return offset;
   }
}
