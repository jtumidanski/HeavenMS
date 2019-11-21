package npc

import config.YamlConfig
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201101 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (YamlConfig.config.server.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
         cm.openShopNPC(9201101)
      } else {
         //cm.sendOk("The patrol in New Leaf City is always ready. No creatures are able to break through to the city.");
         cm.sendDefault()
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201101(cm: cm))
   }
   return (NPC9201101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }