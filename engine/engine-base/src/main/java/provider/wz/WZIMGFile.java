package provider.wz;

import java.awt.Point;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.RandomAccessByteStream;
import tools.data.input.SeekableLittleEndianAccessor;

public class WZIMGFile {
   private WZFileEntry file;
   private WZIMGEntry root;
   private boolean provideImages;
   @SuppressWarnings("unused")
   private boolean modernImg;

   public WZIMGFile(File wzFile, WZFileEntry file, boolean provideImages, boolean modernImg) throws IOException {
      RandomAccessFile raf = new RandomAccessFile(wzFile, "r");
      SeekableLittleEndianAccessor accessor = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));
      accessor.seek(file.getOffset());
      this.file = file;
      this.provideImages = provideImages;
      root = new WZIMGEntry(file.getParent());
      root.setName(file.getName());
      root.setType(MapleDataType.EXTENDED);
      this.modernImg = modernImg;
      parseExtended(root, accessor, 0);
      root.finish();
      raf.close();
   }

   protected void dumpImg(OutputStream out, SeekableLittleEndianAccessor accessor) throws IOException {
      DataOutputStream os = new DataOutputStream(out);
      long oldPos = accessor.getPosition();
      accessor.seek(file.getOffset());
      for (int x = 0; x < file.getSize(); x++) {
         os.write(accessor.readByte());
      }
      accessor.seek(oldPos);
   }

   public WZIMGEntry getRoot() {
      return root;
   }

   private void parse(WZIMGEntry entry, SeekableLittleEndianAccessor accessor) {
      byte marker = accessor.readByte();
      switch (marker) {
         case 0: {
            String name = WZTool.readDecodedString(accessor);
            entry.setName(name);
            break;
         }
         case 1: {
            String name = WZTool.readDecodedStringAtOffsetAndReset(accessor, file.getOffset() + accessor.readInt());
            entry.setName(name);
            break;
         }
         default:
            System.out.println("Unknown Image identifier: " + marker + " at offset " + (accessor.getPosition() - file.getOffset()));
      }
      marker = accessor.readByte();
      switch (marker) {
         case 0:
            entry.setType(MapleDataType.IMG_0x00);
            break;
         case 2:
         case 11: //??? no idea, since 0.49
            entry.setType(MapleDataType.SHORT);
            entry.setData(accessor.readShort());
            break;
         case 3:
            entry.setType(MapleDataType.INT);
            entry.setData(WZTool.readValue(accessor));
            break;
         case 4:
            entry.setType(MapleDataType.FLOAT);
            entry.setData(WZTool.readFloatValue(accessor));
            break;
         case 5:
            entry.setType(MapleDataType.DOUBLE);
            entry.setData(accessor.readDouble());
            break;
         case 8:
            entry.setType(MapleDataType.STRING);
            byte iMarker = accessor.readByte();
            if (iMarker == 0) {
               entry.setData(WZTool.readDecodedString(accessor));
            } else if (iMarker == 1) {
               entry.setData(WZTool.readDecodedStringAtOffsetAndReset(accessor, accessor.readInt() + file.getOffset()));
            } else {
               System.out.println("Unknown String type " + iMarker);
            }
            break;
         case 9:
            entry.setType(MapleDataType.EXTENDED);
            long endOfExtendedBlock = accessor.readInt();
            endOfExtendedBlock += accessor.getPosition();
            parseExtended(entry, accessor, endOfExtendedBlock);
            break;
         default:
            System.out.println("Unknown Image type " + marker);
      }
   }

   private void parseExtended(WZIMGEntry entry, SeekableLittleEndianAccessor accessor, long endOfExtendedBlock) {
      byte marker = accessor.readByte();
      String type;
      switch (marker) {
         case 0x73:
            type = WZTool.readDecodedString(accessor);
            break;
         case 0x1B:
            type = WZTool.readDecodedStringAtOffsetAndReset(accessor, file.getOffset() + accessor.readInt());
            break;
         default:
            throw new RuntimeException("Unknown extended image identifier: " + marker + " at offset " +
                  (accessor.getPosition() - file.getOffset()));
      }
      switch (type) {
         case "Property": {
            parseExtendedProperty(entry, accessor);
            break;
         }
         case "Canvas": {
            parseExtendedCanvas(entry, accessor);
            break;
         }
         case "Shape2D#Vector2D":
            parseExtendedVectorShape(entry, accessor);
            break;
         case "Shape2D#Convex2D": {
            parseExtendedConvexShape(entry, accessor);
            break;
         }
         case "Sound_DX8": {
            parseExtendedSound(entry, accessor, endOfExtendedBlock);
            break;
         }
         case "UOL":
            parseExtendedUOL(entry, accessor);
            break;
         default:
            throw new RuntimeException("Unhandled extended type: " + type);
      }
   }

   private void parseExtendedProperty(WZIMGEntry entry, SeekableLittleEndianAccessor accessor) {
      entry.setType(MapleDataType.PROPERTY);
      accessor.readByte();
      accessor.readByte();
      int children = WZTool.readValue(accessor);
      for (int i = 0; i < children; i++) {
         WZIMGEntry cEntry = new WZIMGEntry(entry);
         parse(cEntry, accessor);
         cEntry.finish();
         entry.addChild(cEntry);
      }
   }

   private void parseExtendedCanvas(WZIMGEntry entry, SeekableLittleEndianAccessor accessor) {
      byte marker;
      entry.setType(MapleDataType.CANVAS);
      accessor.readByte();
      marker = accessor.readByte();
      if (marker == 0) {
         // do nothing
      } else if (marker == 1) {
         accessor.readByte();
         accessor.readByte();
         int children = WZTool.readValue(accessor);
         for (int i = 0; i < children; i++) {
            WZIMGEntry child = new WZIMGEntry(entry);
            parse(child, accessor);
            child.finish();
            entry.addChild(child);
         }
      } else {
         System.out.println("Canvas marker != 1 (" + marker + ")");
      }
      int width = WZTool.readValue(accessor);
      int height = WZTool.readValue(accessor);
      int format = WZTool.readValue(accessor);
      int format2 = accessor.readByte();
      accessor.readInt();
      int dataLength = accessor.readInt() - 1;
      accessor.readByte();
      if (provideImages) {
         byte[] pngData = accessor.read(dataLength);
         entry.setData(new PNGMapleCanvas(width, height, dataLength, format + format2, pngData));
      } else {
         entry.setData(new PNGMapleCanvas(width, height, dataLength, format + format2, null));
         accessor.skip(dataLength);
      }
   }

   private void parseExtendedVectorShape(WZIMGEntry entry, SeekableLittleEndianAccessor accessor) {
      entry.setType(MapleDataType.VECTOR);
      int x = WZTool.readValue(accessor);
      int y = WZTool.readValue(accessor);
      entry.setData(new Point(x, y));
   }

   private void parseExtendedConvexShape(WZIMGEntry entry, SeekableLittleEndianAccessor accessor) {
      int children = WZTool.readValue(accessor);
      for (int i = 0; i < children; i++) {
         WZIMGEntry cEntry = new WZIMGEntry(entry);
         parseExtended(cEntry, accessor, 0);
         cEntry.finish();
         entry.addChild(cEntry);
      }
   }

   private void parseExtendedSound(WZIMGEntry entry, SeekableLittleEndianAccessor accessor, long endOfExtendedBlock) {
      entry.setType(MapleDataType.SOUND);
      accessor.readByte();
      int dataLength = WZTool.readValue(accessor);
      WZTool.readValue(accessor); // no clue what this is

      int offset = (int) accessor.getPosition();
      entry.setData(new ImgMapleSound(dataLength, offset - file.getOffset()));
      accessor.seek(endOfExtendedBlock);
   }

   private void parseExtendedUOL(WZIMGEntry entry, SeekableLittleEndianAccessor accessor) {
      entry.setType(MapleDataType.UOL);
      accessor.readByte();
      byte uolMarker = accessor.readByte();
      switch (uolMarker) {
         case 0:
            entry.setData(WZTool.readDecodedString(accessor));
            break;
         case 1:
            entry.setData(WZTool.readDecodedStringAtOffsetAndReset(accessor, file.getOffset() + accessor.readInt()));
            break;
         default:
            System.out.println("Unknown UOL marker: " + uolMarker + " " + entry.getName());
      }
   }
}
