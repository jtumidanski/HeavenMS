package server.quest;

public enum MapleQuestRequirementType {
   UNDEFINED(-1), JOB(0), ITEM(1), QUEST(2), MIN_LEVEL(3), MAX_LEVEL(4), END_DATE(5), MOB(6), NPC(7), FIELD_ENTER(8), INTERVAL(9), SCRIPT(10), PET(11), MIN_PET_TAMENESS(12), MONSTER_BOOK(13), NORMAL_AUTO_START(14), INFO_NUMBER(15), INFO_EX(16), COMPLETED_QUEST(17), START(18), END(19), DAY_BY_DAY(20), MESO(21), BUFF(22), EXCEPT_BUFF(23);
   final byte type;

   MapleQuestRequirementType(int type) {
      this.type = (byte) type;
   }

   public static MapleQuestRequirementType getByWZName(String name) {
      return switch (name) {
         case "job" -> JOB;
         case "quest" -> QUEST;
         case "item" -> ITEM;
         case "lvmin" -> MIN_LEVEL;
         case "lvmax" -> MAX_LEVEL;
         case "end" -> END_DATE;
         case "mob" -> MOB;
         case "npc" -> NPC;
         case "fieldEnter" -> FIELD_ENTER;
         case "interval" -> INTERVAL;
         case "startscript", "endscript" -> SCRIPT;
         case "pet" -> PET;
         case "pettamenessmin" -> MIN_PET_TAMENESS;
         case "mbmin" -> MONSTER_BOOK;
         case "normalAutoStart" -> NORMAL_AUTO_START;
         case "infoNumber" -> INFO_NUMBER;
         case "infoex" -> INFO_EX;
         case "questComplete" -> COMPLETED_QUEST;
         case "start" -> START;
         case "daybyday" -> DAY_BY_DAY;
         case "money" -> MESO;
         case "buff" -> BUFF;
         case "exceptbuff" -> EXCEPT_BUFF;
         default -> UNDEFINED;
      };
   }

   public byte getType() {
      return type;
   }
}
