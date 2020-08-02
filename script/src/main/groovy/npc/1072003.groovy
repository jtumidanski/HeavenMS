package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1072003 {
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
            if (cm.isQuestCompleted(100010)) {
               cm.sendOk(I18nMessage.from("1072003_TRUE_HERO"))
               cm.dispose()
            } else if (cm.isQuestCompleted(100009)) {
               cm.sendNext(I18nMessage.from("1072003_ILL_LET_YOU_IN"))
               status = 3
            } else if (cm.isQuestStarted(100009)) {
               cm.sendNext(I18nMessage.from("1072003_ISNT_THIS_A_LETTER"))
            } else {
               cm.sendOk(I18nMessage.from("1072003_ONCE_YOU_ARE_READY"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("1072003_SO_YOU_WANT_TO"))
         } else if (status == 2) {
            cm.sendAcceptDecline(I18nMessage.from("1072003_IF_YOU_ARE_READY"))
         } else if (status == 3) {
            cm.sendOk(I18nMessage.from("1072003_COLLECT_MARBLES"))
            cm.completeQuest(100009)
            cm.startQuest(100010)
            cm.gainItem(4031011, (short) -1)
         } else if (status == 4) {
            cm.warp(108000400, 0)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC1072003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072003(cm: cm))
   }
   return (NPC1072003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }