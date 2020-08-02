package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000012 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      cm.sendSimple(I18nMessage.from("9000012_IT_IS_HOT"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
      }
      if (status == 0) {
         if (selection == 0) {
            cm.sendYesNo(I18nMessage.from("9000012_IF_YOU_LEAVE_NOW"))
         } else if (selection == 1) {
            if (cm.getMeso() < 1 && !cm.canHold(1322005)) {
               cm.sendOk(I18nMessage.from("9000012_NOT_ENOUGH_MESOS_OR_INVENTORY_SPACE"))
               cm.dispose()
            } else {
               cm.gainItem(1322005)
               cm.gainMeso(-1)
               cm.dispose()
            }
         }
      } else if (status == 1) {
         if (cm.getEvent() != null) {
            cm.getEvent().addLimit()
         }
         cm.warp(109050001, 0)
         cm.dispose()
      }
   }
}

NPC9000012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000012(cm: cm))
   }
   return (NPC9000012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }