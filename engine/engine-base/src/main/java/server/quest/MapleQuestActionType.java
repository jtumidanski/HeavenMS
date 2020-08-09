package server.quest;

public enum MapleQuestActionType {
   UNDEFINED(-1), EXP(0), ITEM(1), NEXT_QUEST(2), MESO(3), QUEST(4), SKILL(5), FAME(6), BUFF(7), PET_SKILL(8), YES(9), NO(10), NPC(11), MIN_LEVEL(12), NORMAL_AUTO_START(13), PET_TAMENESS(14), PET_SPEED(15), INFO(16), ZERO(16);
   final byte type;

   MapleQuestActionType(int type) {
      this.type = (byte) type;
   }

   public static MapleQuestActionType getByWZName(String name) {
      return switch (name) {
         case "exp" -> EXP;
         case "money" -> MESO;
         case "item" -> ITEM;
         case "skill" -> SKILL;
         case "nextQuest" -> NEXT_QUEST;
         case "pop" -> FAME;
         case "buffItemID" -> BUFF;
         case "petskill" -> PET_SKILL;
         case "no" -> NO;
         case "yes" -> YES;
         case "npc" -> NPC;
         case "lvmin" -> MIN_LEVEL;
         case "normalAutoStart" -> NORMAL_AUTO_START;
         case "pettameness" -> PET_TAMENESS;
         case "petspeed" -> PET_SPEED;
         case "info" -> INFO;
         case "0" -> ZERO;
         default -> UNDEFINED;
      };
   }
}
