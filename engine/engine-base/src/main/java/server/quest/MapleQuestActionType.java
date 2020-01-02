package server.quest;

public enum MapleQuestActionType {
   UNDEFINED(-1), EXP(0), ITEM(1), NEXT_QUEST(2), MESO(3), QUEST(4), SKILL(5), FAME(6), BUFF(7), PET_SKILL(8), YES(9), NO(10), NPC(11), MIN_LEVEL(12), NORMAL_AUTO_START(13), PET_TAMENESS(14), PET_SPEED(15), INFO(16), ZERO(16);
   final byte type;

   MapleQuestActionType(int type) {
      this.type = (byte) type;
   }

   public static MapleQuestActionType getByWZName(String name) {
      switch (name) {
         case "exp":
            return EXP;
         case "money":
            return MESO;
         case "item":
            return ITEM;
         case "skill":
            return SKILL;
         case "nextQuest":
            return NEXT_QUEST;
         case "pop":
            return FAME;
         case "buffItemID":
            return BUFF;
         case "petskill":
            return PET_SKILL;
         case "no":
            return NO;
         case "yes":
            return YES;
         case "npc":
            return NPC;
         case "lvmin":
            return MIN_LEVEL;
         case "normalAutoStart":
            return NORMAL_AUTO_START;
         case "pettameness":
            return PET_TAMENESS;
         case "petspeed":
            return PET_SPEED;
         case "info":
            return INFO;
         case "0":
            return ZERO;
         default:
            return UNDEFINED;
      }
   }
}
