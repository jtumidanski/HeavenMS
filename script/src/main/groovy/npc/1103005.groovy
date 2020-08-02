package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1103005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendAcceptDecline(I18nMessage.from("1103005_WHAT_DO_YOU_THINK"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         cm.warp(130000000)
      } else {
         cm.warp(cm.getPlayer().getSavedLocation("CYGNUSINTRO"))
      }
      cm.dispose()
   }
}

NPC1103005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1103005(cm: cm))
   }
   return (NPC1103005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }