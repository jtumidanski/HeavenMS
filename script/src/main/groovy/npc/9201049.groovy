package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201049 {
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
            cm.sendOk("Hey there, did you enjoy the wedding? I will head you back to #bAmoria#k now.")
         } else if (status == 1) {
            EventInstanceManager eim = cm.getEventInstance()
            if (eim != null) {
               int boxId = (cm.getPlayer().getId() == eim.getIntProperty("groomId") || cm.getPlayer().getId() == eim.getIntProperty("brideId")) ? 4031424 : 4031423

               if (cm.canHold(boxId, 1)) {
                  cm.gainItem(boxId, (short) 1)
                  cm.warp(680000000)
                  cm.sendOk("You just received an Onyx Chest. Search for #b#p9201014##k, she is at the top of Amoria, she knows how to open these.")
               } else {
                  cm.sendOk("Please make room on your ETC inventory to receive the Onyx Chest.")
                  cm.dispose()
                  return
               }
            } else {
               cm.warp(680000000)
            }

            cm.dispose()
         }
      }
   }
}

NPC9201049 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201049(cm: cm))
   }
   return (NPC9201049) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }