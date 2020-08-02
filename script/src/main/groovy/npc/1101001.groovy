package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import tools.I18nMessage

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
         cm.sendOk(I18nMessage.from("1101001_CAST_BLESSING"))
      } else {
         cm.sendOk(I18nMessage.from("1101001_DO_NOT_STOP_TRAINING"))
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