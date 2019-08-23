package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2110005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String toMagatia = "Would you like to take the #bCamel Cab#k to #bMagatia#k, the town of Alchemy? The fare is #b1500 mesos#k."
   String toAriant = "Would you like to take the #bCamel Cab#k to #bAriant#k, the town of Burning Roads? The fare is #b1500 mesos#k."

   def start() {
      cm.sendYesNo(cm.getPlayer().getMapId() == 260020000 ? toMagatia : toAriant)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         if (cm.getMeso() < 1500) {
            cm.sendNext("I am sorry, but I think you are short on mesos. I am afraid I can't let you ride this if you do not have enough money to do so. Please come back when you have enough money to use this.")
            cm.dispose()
         } else {
            cm.warp(cm.getPlayer().getMapId() == 260020000 ? 261000000 : 260000000, 0)
            cm.gainMeso(-1500)
            cm.dispose()
         }
      } else if (mode == 0) {
         cm.sendNext("Hmmm... too busy to do it right now? If you feel like doing it, though, come back and find me.")
         cm.dispose()
      }
   }
}

NPC2110005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2110005(cm: cm))
   }
   return (NPC2110005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }