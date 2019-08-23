package npc

import constants.GameConstants
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1101001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().isCygnus() && GameConstants.getJobBranch(cm.getJob()) > 2) {
         cm.useItem(2022458)
         cm.sendOk("Let me cast you my blessings, my Knight. Please protect the world of Maple....")
      } else {
         cm.sendOk("Don't stop training. Every ounce of your energy is required to protect the world of Maple....")
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1101001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1101001(cm: cm))
   }
   return (NPC1101001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }