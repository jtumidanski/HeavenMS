package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Kiridu
	Map(s): 		Cygnus
	Description: 	
*/


class NPC1102002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Yo. I am #p1102002#, in charge of mount raising and training for the Cygnus Knights' of Ereve!")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1102002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1102002(cm: cm))
   }
   return (NPC1102002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }