package provider.wz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntity;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;

public class WZDirectoryEntry extends WZEntry implements MapleDataDirectoryEntry {
   private List<MapleDataDirectoryEntry> subDirectoryEntries = new ArrayList<>();
   private List<MapleDataFileEntry> files = new ArrayList<>();
   private Map<String, MapleDataEntry> entries = new HashMap<>();

   public WZDirectoryEntry(String name, int size, int checksum, MapleDataEntity parent) {
      super(name, size, checksum, parent);
   }

   public WZDirectoryEntry() {
      super(null, 0, 0, null);
   }

   public void addDirectory(MapleDataDirectoryEntry dir) {
      subDirectoryEntries.add(dir);
      entries.put(dir.getName(), dir);
   }

   public void addFile(MapleDataFileEntry fileEntry) {
      files.add(fileEntry);
      entries.put(fileEntry.getName(), fileEntry);
   }

   public List<MapleDataDirectoryEntry> getSubdirectories() {
      return Collections.unmodifiableList(subDirectoryEntries);
   }

   public List<MapleDataFileEntry> getFiles() {
      return Collections.unmodifiableList(files);
   }

   public MapleDataEntry getEntry(String name) {
      return entries.get(name);
   }
}
