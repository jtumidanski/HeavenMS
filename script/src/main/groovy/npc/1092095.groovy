package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092095 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031847)) {
         cm.sendNext("The hungry calf is drinking all the milk! The bottle remains empty...")
      } else if (cm.haveItem(4031848) || cm.haveItem(4031849) || cm.haveItem(4031850)) {
         cm.sendNext("The hungry calf is drinking all the milk! The bottle is now empty.")
         if (cm.haveItem(4031848)) {
            cm.gainItem(4031848, (short) -1)
         } else if (cm.haveItem(4031849)) {
            cm.gainItem(4031849, (short) -1)
         } else {
            cm.gainItem(4031850, (short) -1)
         }
         cm.gainItem(4031847, (short) 1)
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         status--
         start()
      } else {
         status++
      }
      if (status == 0) {
         cm.sendPrev("The hungry calf isn't interested in the empty bottle.")
      } else if (status == 1) {
         cm.dispose()
      }
   }
}

NPC1092095 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092095(cm: cm))
   }
   return (NPC1092095) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }