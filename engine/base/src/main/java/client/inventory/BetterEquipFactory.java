package client.inventory;

import server.MapleItemInformationProvider;

public class BetterEquipFactory {
   private static BetterEquipFactory instance;

   public static BetterEquipFactory getInstance() {
      if (instance == null) {
         instance = new BetterEquipFactory();
      }
      return instance;
   }

   private BetterEquipFactory() {
   }

   public Equip create(int id, short position) {
      return create(id, position, 0);
   }

   public Equip create(int id, short position, int slots) {
      boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(id, false) > 1);
      return new Equip(id, position, slots, isElemental);
   }
}