package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 	Ali
	Map(s): 		Adobis's Mission I: The Room of Tragedy
	Description: 	Zakum Quest NPC Exit
*/


class NPC2030011 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.warp(211042300)
      cm.removeAll(4001015)
      cm.removeAll(4001016)
      cm.removeAll(4001018)
      cm.sendOk(I18nMessage.from("2030011_SEE_YOU_NEXT_TIME"))
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2030011 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030011(cm: cm))
   }
   return (NPC2030011) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }