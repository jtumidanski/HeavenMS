package npc

import config.YamlConfig
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/

class NPCmapleTV {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (YamlConfig.config.server.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
         cm.dispose()
         cm.openNpc(9201088, "scroll_generator")
         return
      }

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
            // do nothing
            cm.dispose()
         }
      }
   }
}

NPCmapleTV getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPCmapleTV(cm: cm))
   }
   return (NPCmapleTV) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }