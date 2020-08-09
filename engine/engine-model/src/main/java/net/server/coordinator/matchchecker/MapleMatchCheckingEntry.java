package net.server.coordinator.matchchecker;

public class MapleMatchCheckingEntry {
   private final Integer cid;

   private Boolean accepted = false;

   public MapleMatchCheckingEntry(Integer cid) {
      this.cid = cid;
   }

   public Integer cid() {
      return cid;
   }

   public Boolean accepted() {
      return accepted;
   }

   public Boolean accept() {
      if (!accepted) {
         accepted = true;
         return true;
      }
      return false;
   }
}
