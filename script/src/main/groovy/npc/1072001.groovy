package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 	Magician Job Instructor
	Map(s): 		Victoria Road : The Forest North of Ellinia
	Description: 	Magician 2nd Job Advancement
*/


class NPC1072001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.isQuestCompleted(100007)) {
               cm.sendOk(I18nMessage.from("1072001_TRUE_HERO"))
               cm.dispose()
            } else if (cm.isQuestCompleted(100006)) {
               cm.sendNext(I18nMessage.from("1072001_ILL_LET_YOU_IN"))
               status = 4
            } else if (cm.isQuestStarted(100006)) {
               cm.sendNext(I18nMessage.from("1072001_EXPLAIN_THE_TEST"))
            } else {
               cm.sendOk(I18nMessage.from("1072001_ONCE_YOU_ARE_READY"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("1072001_SEND_YOU_TO_A_HIDDEN_MAP"))
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("1072001_ACQUIRE_MARBLE"))
         } else if (status == 3) {
            cm.sendYesNo(I18nMessage.from("1072001_CANNOT_LEAVE_UNTIL_COMPLETE"))
         } else if (status == 4) {
            cm.sendNext(I18nMessage.from("1072001_ILL_LET_YOU_IN"))
            cm.completeQuest(100006)
            cm.startQuest(100007)
            cm.gainItem(4031009, (short) -1)
         } else if (status == 5) {
            cm.warp(108000200, 0)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC1072001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072001(cm: cm))
   }
   return (NPC1072001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }