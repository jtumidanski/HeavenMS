package provider.wz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.InputStreamByteStream;
import tools.data.input.LittleEndianAccessor;
import tools.data.input.RandomAccessByteStream;
import tools.data.input.SeekableLittleEndianAccessor;

public class WZFile implements MapleDataProvider {
   static {
      ListWZFile.init();
   }

   private File wzFile;
   private LittleEndianAccessor lea;
   private SeekableLittleEndianAccessor accessor;
   private int headerSize;
   private WZDirectoryEntry root;
   private boolean provideImages;
   private int cOffset;

   public WZFile(File wzFile, boolean provideImages) throws IOException {
      this.wzFile = wzFile;
      lea = new GenericLittleEndianAccessor(new InputStreamByteStream(new BufferedInputStream(new FileInputStream(wzFile))));
      RandomAccessFile raf = new RandomAccessFile(wzFile, "r");
      accessor = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));
      root = new WZDirectoryEntry(wzFile.getName(), 0, 0, null);
      this.provideImages = provideImages;
      load();
   }

   private void load() {
      lea.readAsciiString(4);
      lea.readInt();
      lea.readInt();
      headerSize = lea.readInt();
      lea.readNullTerminatedAsciiString();
      lea.readShort();
      parseDirectory(root);
      cOffset = (int) lea.getBytesRead();
      getOffsets(root);
   }

   private void getOffsets(MapleDataDirectoryEntry directoryEntry) {
      directoryEntry.getFiles().forEach(file -> {
         file.setOffset(cOffset);
         cOffset += file.getSize();
      });
      directoryEntry.getSubdirectories().forEach(this::getOffsets);
   }

   private void parseDirectory(WZDirectoryEntry directoryEntry) {
      int entries = WZTool.readValue(lea);
      for (int i = 0; i < entries; i++) {
         byte marker = lea.readByte();
         String name;
         int size, checksum;
         switch (marker) {
            case 0x02:
               name = WZTool.readDecodedStringAtOffsetAndReset(accessor, lea.readInt() + this.headerSize + 1);
               size = WZTool.readValue(lea);
               checksum = WZTool.readValue(lea);
               lea.readInt(); //dummy int
               directoryEntry.addFile(new WZFileEntry(name, size, checksum, directoryEntry));
               break;
            case 0x03:
            case 0x04:
               name = WZTool.readDecodedString(lea);
               size = WZTool.readValue(lea);
               checksum = WZTool.readValue(lea);
               lea.readInt(); //dummy int
               if (marker == 3) {
                  directoryEntry.addDirectory(new WZDirectoryEntry(name, size, checksum, directoryEntry));
               } else {
                  directoryEntry.addFile(new WZFileEntry(name, size, checksum, directoryEntry));
               }
               break;
            default:
         }
      }
      directoryEntry.getSubdirectories().forEach(subDirectoryEntry -> parseDirectory((WZDirectoryEntry) subDirectoryEntry));
   }

   public WZIMGFile getImgFile(String path) throws IOException {
      String[] segments = path.split("/");
      WZDirectoryEntry dir = root;
      for (int x = 0; x < segments.length - 1; x++) {
         dir = (WZDirectoryEntry) dir.getEntry(segments[x]);
         if (dir == null) {
            return null;
         }
      }
      WZFileEntry entry = (WZFileEntry) dir.getEntry(segments[segments.length - 1]);
      if (entry == null) {
         return null;
      }
      String fullPath = wzFile.getName().substring(0, wzFile.getName().length() - 3).toLowerCase() + "/" + path;
      return new WZIMGFile(this.wzFile, entry, provideImages, ListWZFile.isModernImgFile(fullPath));
   }

   @Override
   public synchronized MapleData getData(String path) {
      try {
         WZIMGFile imgFile = getImgFile(path);
         if (imgFile == null) {
            return null;
         }
         return imgFile.getRoot();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }

   @Override
   public MapleDataDirectoryEntry getRoot() {
      return root;
   }
}
