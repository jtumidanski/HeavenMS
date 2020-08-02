package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || !cm.isQuestStarted(2180)) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendNext(I18nMessage.from("1092000_SEND_YOU_TO_THE_STABLE"))
         } else if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("1092000_I_GET_CONFUSED"))
         } else if (status == 2) {
            if (cm.canHold(4031847)) {
               cm.gainItem(4031847, (short) 1)
               cm.warp(912000100, 0)
            } else {
               cm.sendOk(I18nMessage.from("1092000_INVENTORY_IS_FULL"))
            }
            cm.dispose()
         }
      }
   }
}

NPC1092000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092000(cm: cm))
   }
   return (NPC1092000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }