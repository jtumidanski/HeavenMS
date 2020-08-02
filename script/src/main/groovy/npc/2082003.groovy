package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2082003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple(I18nMessage.from("2082003_IF_YOU_HAD_WINGS"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         cm.useItem(2210016)
         cm.warp(200090500, 0)
      }
      cm.dispose()
   }
}

NPC2082003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2082003(cm: cm))
   }
   return (NPC2082003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }