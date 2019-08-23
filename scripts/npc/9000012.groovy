package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000012 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      cm.sendSimple("Man... It is hot!!!~ How can I help you?\r\n#L0##bLeave the event game.#l\r\n#L1#Buy the weapon (Wooden Club 1 meso)")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
      }
      if (status == 0) {
         if (selection == 0) {
            cm.sendYesNo("If you leave now, you can't participate in this event for the next 24 hours. Are you sure you want to leave?")
         } else if (selection == 1) {
            if (cm.getMeso() < 1 && !cm.canHold(1322005)) {
               cm.sendOk("You don't have enough mesos or you don't have any space in your inventory.")
               cm.dispose()
            } else {
               cm.gainItem(1322005)
               cm.gainMeso(-1)
               cm.dispose()
            }
         }
      } else if (status == 1) {
         if (cm.getEvent() != null) {
            cm.getEvent().addLimit()
         }
         cm.warp(109050001, 0)
         cm.dispose()
      }
   }
}

NPC9000012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000012(cm: cm))
   }
   return (NPC9000012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }