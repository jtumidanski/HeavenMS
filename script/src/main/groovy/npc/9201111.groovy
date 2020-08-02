package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201111 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().getMap().getId() == 610030500) {
         cm.sendOk(I18nMessage.from("9201111_DIG_FOR_BOOTY"))
         cm.dispose()
      } else if (cm.getPlayer().getMap().getId() == 610030000) {
         cm.sendOk(I18nMessage.from("9201111_LONG_AGO"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201111 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201111(cm: cm))
   }
   return (NPC9201111) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }