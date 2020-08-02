package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Warrior Job Instructor
	Map(s): 		Victoria Road : West Rocky Mountain IV
	Description: 	Warrior 2nd Job Advancement
*/


class NPC1072000 {
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
            if (cm.isQuestCompleted(100004)) {
               cm.sendOk(I18nMessage.from("1072000_TRUE_HERO"))
               cm.dispose()
            } else if (cm.isQuestCompleted(100003)) {
               cm.sendNext(I18nMessage.from("1072000_ILL_LET_YOU_IN"))
               status = 4
            } else if (cm.isQuestStarted(100003)) {
               cm.sendNext(I18nMessage.from("1072000_EXPLAIN_THE_TEST"))
            } else {
               cm.sendOk(I18nMessage.from("1072000_ONCE_YOU_ARE_READY"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("1072000_SEND_YOU_TO_A_HIDDEN_MAP"))
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("1072000_ACQUIRE_MARBLE"))
         } else if (status == 3) {
            cm.sendYesNo(I18nMessage.from("1072000_CANNOT_LEAVE_UNTIL_COMPLETE"))
         } else if (status == 4) {
            cm.sendNext(I18nMessage.from("1072000_ILL_LET_YOU_IN"))
            cm.completeQuest(100003)
            cm.startQuest(100004)
            cm.gainItem(4031008, (short) -1)
         } else if (status == 5) {
            cm.warp(108000300, 0)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC1072000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072000(cm: cm))
   }
   return (NPC1072000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }