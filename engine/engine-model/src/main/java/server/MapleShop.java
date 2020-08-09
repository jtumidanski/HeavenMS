package server;

import java.util.Optional;

public class MapleShop {
   private final Integer id;

   private final Integer npcId;

   private MapleShopItem[] items = new MapleShopItem[0];

   public MapleShop(Integer id, Integer npcId) {
      this.id = id;
      this.npcId = npcId;
   }

   public Optional<MapleShopItem> findBySlot(Short slot) {
      if (slot < 0 || slot >= items.length) {
         return Optional.empty();
      }
      return Optional.of(items[slot]);
   }

   public void setItems(MapleShopItem[] items) {
      this.items = items;
   }

   public MapleShopItem[] items() {
      return items;
   }

   public Integer id() {
      return id;
   }

   public Integer npcId() {
      return npcId;
   }

   public Integer tokenValue() {
      return 1000000000;
   }

   public Integer token() {
      return 4000313;
   }
}
