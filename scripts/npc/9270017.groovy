package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270017 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendYesNo("The plane will be taking off soon, will you leave now? You will have to buy the plane ticket again to come in here.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode != 1) {
         if (mode == 0) {
            cm.sendOk("Please hold on for a sec, and plane will be taking off. Thanks for your patience.")
         }
         cm.dispose()
         return
      }
      status++
      if (status == 1) {
         cm.sendNext("The ticket is not refundable, hope to see you again!")
      } else if (status == 2) {
         cm.warp(103000000)
         cm.dispose()
      }
   }
}

NPC9270017 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270017(cm: cm))
   }
   return (NPC9270017) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }