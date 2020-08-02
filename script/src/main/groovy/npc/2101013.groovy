package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] towns = [100000000, 101000000, 102000000, 103000000, 104000000]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendNext(I18nMessage.from("2101013_SCARED_OF_SPEED_OR_HEIGHTS"))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendNext(I18nMessage.from("2101013_YOU_CAME_TO_THE_RIGHT_PLACE"))
         } else if (status == 1) {
            cm.sendYesNo(I18nMessage.from("2101013_PLEASE_REMEMBER"))
         } else if (status == 2) {
            cm.sendNext(I18nMessage.from("2101013_READY_TO_TAKEOFF"))
         } else if (status == 3) {
            if (cm.getMeso() >= 10000) {
               cm.gainMeso(-10000)
               cm.warp(towns[Math.floor(Math.random() * towns.length).intValue()])
            } else {
               cm.sendNextPrev(I18nMessage.from("2101013_SHORT_ON_CASH"))
               cm.dispose()
            }
         }
      }
   }
}

NPC2101013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101013(cm: cm))
   }
   return (NPC2101013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }