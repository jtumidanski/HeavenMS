package npc

import config.YamlConfig
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2082014 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
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
            if (YamlConfig.config.server.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
               cm.openShopNPC(2082014)
            } else if (cm.isQuestStarted(3749)) {
               cm.sendOk("We've already located the enemy's ultimate weapon! Follow along the ship's bow area ahead and you will find my sister #b#p2082013##k. Report to her for further instructions on the mission.")
            } else {
               cm.sendDefault()
            }

            cm.dispose()
         }
      }
   }
}

NPC2082014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2082014(cm: cm))
   }
   return (NPC2082014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }