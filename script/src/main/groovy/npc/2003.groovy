package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Robin
	Map(s): 		Maple Road : Snail Hunting Ground I (40000)
	Description: 		Beginner Helper
*/

class NPC2003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      sel = -1
      cm.sendSimple(I18nMessage.from("2003_ASK_ME_ANY_QUESTIONS"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type != 4) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         if (sel == -1) {
            sel = selection
         }
         if (sel == 0) {
            cm.sendNext(I18nMessage.from("2003_HOW_TO_MOVE"))
         } else if (sel == 1) {
            cm.sendNext(I18nMessage.from("2003_HOW_TO_TAKE_DOWN_A_MONSTER"))
         } else if (sel == 2) {
            cm.sendNext(I18nMessage.from("2003_HOW_TO_GATHER"))
         } else if (sel == 3) {
            cm.sendNext(I18nMessage.from("2003_WHEN_YOU_DIE"))
         } else if (sel == 4) {
            cm.sendNext(I18nMessage.from("2003_CHOOSING_A_JOB"))
         } else if (sel == 5) {
            cm.sendNext(I18nMessage.from("2003_THE_ISLAND"))
         } else if (sel == 6) {
            cm.sendNext(I18nMessage.from("2003_WARRIOR_TO_DO"))
         } else if (sel == 7) {
            cm.sendNext(I18nMessage.from("2003_BOWMAN_TO_DO"))
         } else if (sel == 8) {
            cm.sendNext(I18nMessage.from("2003_MAGICIAN_TO_DO"))
         } else if (sel == 9) {
            cm.sendNext(I18nMessage.from("2003_THIEF_TO_DO"))
         } else if (sel == 10) {
            cm.sendNext(I18nMessage.from("2003_RAISE_STATS"))
         } else if (sel == 11) {
            cm.sendNext(I18nMessage.from("2003_CHECK_ITEMS"))
         } else if (sel == 12) {
            cm.sendNext(I18nMessage.from("2003_WEAR_ITEMS"))
         } else if (sel == 13) {
            cm.sendNext(I18nMessage.from("2003_CHECK_EQUIPMENT"))
         } else if (sel == 14) {
            cm.sendNext(I18nMessage.from("2003_SPECIAL_ABILITIES"))
         } else if (sel == 15) {
            cm.sendNext(I18nMessage.from("2003_GET_TO_VICTORIA"))
         } else if (sel == 16) {
            cm.sendNext(I18nMessage.from("2003_MESO"))
         }
      } else if (status == 1) {
         if (sel == 0) {
            cm.sendNextPrev(I18nMessage.from("2003_ATTACKING_MONSTERS"))
         } else if (sel == 1) {
            cm.sendNextPrev(I18nMessage.from("2003_JOB_ADVANCEMENT"))
         } else if (sel == 2) {
            cm.sendNextPrev(I18nMessage.from("2003_FULL_INVENTORY"))
         } else if (sel == 3) {
            cm.sendNextPrev(I18nMessage.from("2003_BEGINNER_DEATH"))
         } else if (sel == 4) {
            cm.sendNextPrev(I18nMessage.from("2003_HOW_TO_ADVANCE"))
         } else if (sel == 5) {
            cm.sendNextPrev(I18nMessage.from("2003_POWERFUL_PLAYER"))
         } else if (sel == 8) {
            cm.sendNextPrev(I18nMessage.from("2003_MAGICIAN_SPECIAL"))
         } else if (sel == 10) {
            cm.sendNextPrev(I18nMessage.from("2003_ABILITY_EXPLANATION"))
         } else if (sel == 15) {
            cm.sendNextPrev(I18nMessage.from("2003_ONE_LAST_PIECE"))
         } else {
            start()
         }
      } else {
         start()
      }
   }
}

NPC2003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2003(cm: cm))
   }
   return (NPC2003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }