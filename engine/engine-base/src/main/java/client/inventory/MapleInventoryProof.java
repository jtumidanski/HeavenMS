package client.inventory;

import client.MapleCharacter;

public class MapleInventoryProof extends MapleInventory {

   public MapleInventoryProof(MapleCharacter mc) {
      super(mc, MapleInventoryType.CAN_HOLD, (byte) 0);
   }

   public void cloneContents(MapleInventory inv) {
      inv.lockInventory();
      lock.lock();
      try {
         inventory.clear();
         this.setSlotLimit(inv.getSlotLimit());

         for (Item it : inv.list()) {
            Item item = new Item(it.id(), it.position(), it.quantity());
            inventory.put(item.position(), item);
         }
      } finally {
         lock.unlock();
         inv.unlockInventory();
      }
   }

   public void flushContents() {
      lock.lock();
      try {
         inventory.clear();
      } finally {
         lock.unlock();
      }
   }

   @Override
   protected short addSlot(Item item) {
      if (item == null) {
         return -1;
      }

      lock.lock();
      try {
         short slotId = getNextFreeSlot();
         if (slotId < 0) {
            return -1;
         }
         inventory.put(slotId, item);

         return slotId;
      } finally {
         lock.unlock();
      }
   }

   @Override
   protected void addSlotFromDB(short slot, Item item) {
      lock.lock();
      try {
         inventory.put(slot, item);
      } finally {
         lock.unlock();
      }
   }

   @Override
   public void removeSlot(short slot) {
      lock.lock();
      try {
         inventory.remove(slot);
      } finally {
         lock.unlock();
      }
   }
}
