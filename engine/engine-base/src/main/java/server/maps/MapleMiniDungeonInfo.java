package server.maps;

public enum MapleMiniDungeonInfo {
   CAVE_OF_MUSHROOMS(105050100, 105050101, 30),
   GOLEM_CASTLE_RUINS(105040304, 105040320, 34),
   HILL_OF_SANDSTORMS(260020600, 260020630, 30),
   HENESYS_PIG_FARM(100020000, 100020100, 30),
   DRAKES_BLUE_CAVE(105090311, 105090320, 30),
   DRUMMER_BUNNYS_LAIR(221023400, 221023401, 30),
   THE_ROUND_TABLE_OF_KENTARUS(240020500, 240020512, 30),
   THE_RESTORING_MEMORY(240040511, 240040800, 19),
   NEWT_SECURED_ZONE(240040520, 240040900, 19),
   PILLAGE_OF_TREASURE_ISLAND(251010402, 251010410, 30),
   CRITICAL_ERROR(261020300, 261020301, 30),
   LONGEST_RIDE_ON_BYE_BYE_STATION(551030000, 551030001, 19);

   private int baseId;
   private int dungeonId;
   private int dungeons;

   MapleMiniDungeonInfo(int baseId, int dungeonId, int dungeons) {
      this.baseId = baseId;
      this.dungeonId = dungeonId;
      this.dungeons = dungeons;
   }

   public static boolean isDungeonMap(int map) {
      for (MapleMiniDungeonInfo dungeon : MapleMiniDungeonInfo.values()) {
         if (map >= dungeon.getDungeonId() && map <= dungeon.getDungeonId() + dungeon.getDungeons()) {
            return true;
         }
      }
      return false;
   }

   public static MapleMiniDungeonInfo getDungeon(int map) {
      for (MapleMiniDungeonInfo dungeon : MapleMiniDungeonInfo.values()) {
         if (map >= dungeon.getDungeonId() && map <= dungeon.getDungeonId() + dungeon.getDungeons()) {
            return dungeon;
         }
      }
      return null;
   }

   public int getBase() {
      return baseId;
   }

   public int getDungeonId() {
      return dungeonId;
   }

   public int getDungeons() {
      return dungeons;
   }
}
