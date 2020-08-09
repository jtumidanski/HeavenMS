package client;

public record MapleMount(int itemId, int skillId, int level, int exp, int tiredness, boolean active) {
   public MapleMount(int itemId, int skillId) {
      this(itemId, skillId, 1, 0, 0, true);
   }

   /**
    * 1902000 - Hog
    * 1902001 - Silver Mane
    * 1902002 - Red Draco
    * 1902005 - Mimiana
    * 1902006 - Mimio
    * 1902007 - Shinjou
    * 1902008 - Frog
    * 1902009 - Ostrich
    * 1902010 - Frog
    * 1902011 - Turtle
    * 1902012 - Yeti
    */
   public Integer id() {
      if (itemId < 1903000) {
         return itemId - 1901999;
      }
      return 5;
   }

   public MapleMount updateItemId(int itemId) {
      return new MapleMount(itemId, skillId, level, exp, tiredness, active);
   }

   public MapleMount setActive(boolean active) {
      return new MapleMount(itemId, skillId, level, exp, tiredness, active);
   }

   public MapleMount updateLevel(int newLevel) {
      return new MapleMount(itemId, skillId, newLevel, exp, tiredness, active);
   }

   public MapleMount updateExp(int newExp) {
      return new MapleMount(itemId, skillId, level, newExp, tiredness, active);
   }

   public MapleMount updateTiredness(int newTiredness) {
      if (newTiredness < 0) {
         newTiredness = 0;
      }
      return new MapleMount(itemId, skillId, level, exp, newTiredness, active);
   }

   public MapleMount incrementTiredness() {
      return new MapleMount(itemId, skillId, level, exp, tiredness + 1, active);
   }
}
