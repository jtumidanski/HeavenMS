package server.maps;

import client.inventory.Item;

public class MaplePlayerShopItem {
   private final Item item;

   private Short bundles;

   private final Integer price;

   private Boolean doesExist;

   public MaplePlayerShopItem(Item item, Short bundles, Integer price) {
      this.item = item;
      this.bundles = bundles;
      this.price = price;
      this.doesExist = true;
   }

   public Item item() {
      return item;
   }

   public Integer price() {
      return price;
   }

   public Boolean doesExist() {
      return doesExist;
   }

   public Short bundles() {
      return bundles;
   }

   public void setBundles(Short bundles) {
      this.bundles = bundles;
   }

   public void setDoesExist(Boolean doesExist) {
      this.doesExist = doesExist;
   }
}
