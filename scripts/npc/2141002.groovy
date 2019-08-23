package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2141002 {
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
         if (mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getEventInstance()
         if (!eim.isEventCleared()) {
            if (status == 0) {
               cm.sendYesNo("Do you want to get out now?")
            } else if (status == 1) {
               cm.warp(270050000, 0)
               cm.dispose()
            }

         } else {
            if (status == 0) {
               cm.sendYesNo("Pink Bean has been defeated! You guys sure are true heroes of this land! In no time, Temple of Time will shine again as bright as ever, all thanks to your efforts! Hooray to our heroes!! Are you ready to go now?")
            } else if (status == 1) {
               if (eim.giveEventReward(cm.getPlayer(), 1)) {
                  cm.warp(270050000)
               } else {
                  cm.sendOk("You cannot receive an instance prize without having an empty room in your EQUIP, USE, SET-UP and ETC inventory.")
               }

               cm.dispose()
            }
         }
      }
   }
}

NPC2141002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2141002(cm: cm))
   }
   return (NPC2141002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }