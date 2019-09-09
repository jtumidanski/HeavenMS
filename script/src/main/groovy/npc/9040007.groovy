package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("\"I fought the Rubian and I lost, and now I am imprisoned in the very gate that blocks my path, my body desecrated. However, my old clothing has holy power within. If you can return the clothing to my body, I should be able to open the gate. Please hurry! \r\n- Sharen III \r\n\r\nP.S. I know this is rather picky of me, but can you please return the clothes to my body #bbottom to top#k? Thank you for your services.\"")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9040007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040007(cm: cm))
   }
   return (NPC9040007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }