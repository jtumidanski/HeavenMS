package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9030000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         if (!cm.hasMerchant() && cm.hasMerchantItems()) {
            cm.showFredrick()
            cm.dispose()
         } else {
            if (cm.hasMerchant()) {
               cm.sendOk("You have a Merchant open.")
               cm.dispose()
            } else {
               cm.sendOk("You don't have any items or mesos to be retrieved.")
               cm.dispose()
            }
         }
      }
   }
}

NPC9030000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9030000(cm: cm))
   }
   return (NPC9030000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }