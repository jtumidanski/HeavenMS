package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2093004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String menu
   int cost = 10000

   def start() {
      cm.sendYesNo("Will you move to #b#m230000000##k now? The price is #b" + cost + " mesos#k.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendNext("Hmmm ... too busy to do it right now? If you feel like doing it, though, come back and find me.")
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (cm.getPlayer().getMeso() < cost) {
               cm.sendOk("I don't think you have enough money...")
            } else {
               cm.gainMeso(-cost)
               cm.warp(230000000)
            }
            cm.dispose()
         }
      }
   }
}

NPC2093004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2093004(cm: cm))
   }
   return (NPC2093004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }