package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int price = 300

   def start() {
      cm.sendYesNo("Would you like to enter the bathhouse? That'll be " + price + " mesos for you.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         if (mode == 0) {
            cm.sendOk("Please come back some other time.")
         }
         cm.dispose()
         return
      }
      if (cm.getMeso() < price) {
         cm.sendOk("Please check and see if you have " + price + " mesos to enter this place.")
      } else {
         cm.gainMeso(-price)
         cm.warp(801000100 + 100 * cm.getPlayer().getGender())
      }
      cm.dispose()
   }
}

NPC9120003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120003(cm: cm))
   }
   return (NPC9120003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }