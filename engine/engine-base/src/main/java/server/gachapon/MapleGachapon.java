package server.gachapon;

import server.MapleGachaponItem;
import server.MapleItemInformationProvider;
import tools.Randomizer;

public class MapleGachapon {

   private static final MapleGachapon instance = new MapleGachapon();

   public static MapleGachapon getInstance() {
      return instance;
   }

   public MapleGachaponItem process(int npcId) {
      Gachapon gachapon = Gachapon.getByNpcId(npcId);
      int tier = gachapon.getTier();
      int item = gachapon.getItem(tier);
      return new MapleGachaponItem(tier, item);
   }

   public enum Gachapon {

      GLOBAL(-1, -1, -1, -1, new Global()),
      HENESYS(9100100, 90, 8, 2, new Henesys()),
      ELLINIA(9100101, 90, 8, 2, new Ellinia()),
      PERION(9100102, 90, 8, 2, new Perion()),
      KERNING_CITY(9100103, 90, 8, 2, new KerningCity()),
      SLEEPYWOOD(9100104, 90, 8, 2, new Sleepywood()),
      MUSHROOM_SHRINE(9100105, 90, 8, 2, new MushroomShrine()),
      SHOWA_SPA_MALE(9100106, 90, 8, 2, new ShowaSpaMale()),
      SHOWA_SPA_FEMALE(9100107, 90, 8, 2, new ShowaSpaFemale()),
      LUDIBRIUM(9100108, 90, 8, 2, new Ludibrium()),
      NEW_LEAF_CITY(9100109, 90, 8, 2, new NewLeafCity()),
      EL_NATH(9100110, 90, 8, 2, new ElNath()),
      NAUTILUS_HARBOR(9100117, 90, 8, 2, new NautilusHarbor());

      private static final Gachapon[] values = Gachapon.values();

      private GachaponItems gachapon;
      private int npcId;
      private int common;
      private int uncommon;
      private int rare;

      Gachapon(int npcId, int c, int u, int r, GachaponItems g) {
         this.npcId = npcId;
         this.gachapon = g;
         this.common = c;
         this.uncommon = u;
         this.rare = r;
      }

      public static Gachapon getByNpcId(int npcId) {
         for (Gachapon gachapon : values) {
            if (npcId == gachapon.npcId) {
               return gachapon;
            }
         }
         return null;
      }

      public static String[] getLootInfo() {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         String[] strList = new String[values.length + 1];

         StringBuilder menuStr = new StringBuilder();
         int j = 0;
         for (Gachapon gachapon : values) {
            menuStr.append("#L").append(j).append("#").append(gachapon.name()).append("#l\r\n");
            j++;

            StringBuilder str = new StringBuilder();
            for (int i = 0; i < 3; i++) {
               int[] gachaponItems = gachapon.getItems(i);

               if (gachaponItems.length > 0) {
                  str.append("  #rTier ").append(i).append("#k:\r\n");
                  for (int itemId : gachaponItems) {
                     str.append("   #i").append(itemId).append("#");
                  }

                  str.append("\r\n");
               }
            }
            str.append("\r\n");

            strList[j] = str.toString();
         }
         strList[0] = menuStr.toString();

         return strList;
      }

      private int getTier() {
         int chance = Randomizer.nextInt(common + uncommon + rare) + 1;
         if (chance > common + uncommon) {
            return 2; //Rare
         } else if (chance > common) {
            return 1; //Uncommon
         } else {
            return 0; //Common
         }
      }

      public int[] getItems(int tier) {
         return gachapon.getItems(tier);
      }

      public int getItem(int tier) {
         int[] gachaponItems = getItems(tier);
         int[] global = GLOBAL.getItems(tier);
         int chance = Randomizer.nextInt(gachaponItems.length + global.length);
         return chance < gachaponItems.length ? gachaponItems[chance] : global[chance - gachaponItems.length];
      }
   }
}
