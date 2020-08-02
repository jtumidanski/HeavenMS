package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Trash Can 3
	Map(s): 		
	Description: 	Kerning Subway
*/


class NPC1052111 {
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
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         } else if (mode == 0) {
            status--
         } else {
            status++
         }

         if (status == 0) {
            if (cm.isQuestStarted(20710)) {
               if (!cm.hasItem(4032136)) {
                  if (cm.canHold(4032136)) {
                     cm.gainItem(4032136, (short) 1)
                     cm.sendNext(I18nMessage.from("1052111_FOUND_IN_THE_CAN"))
                  } else {
                     cm.sendOk(I18nMessage.from("1052111_NOT_ENOUGH_SPACE"))
                  }
               } else {
                  cm.sendOk(I18nMessage.from("1052111_JUST_A_TRASH_CAN"))
               }
            } else {
               cm.sendOk(I18nMessage.from("1052111_JUST_A_TRASH_CAN"))
            }
         } else if (status == 1) {
            cm.dispose()
         }
      }
   }
}

NPC1052111 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052111(cm: cm))
   }
   return (NPC1052111) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }