package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092091 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getQuestProgressInt(2180, 1) == 2) {
         cm.sendNext("You have taken milk from this cow recently, check another cow.")
         cm.dispose()
         return
      }

      if (cm.canHold(4031848) && cm.haveItem(4031847)) {
         cm.sendNext("Now filling up the bottle with milk. The bottle is now 1/3 full of milk.")
         cm.gainItem(4031847, (short) -1)
         cm.gainItem(4031848, (short) 1)

         cm.setQuestProgress(2180, 1, 2)
      } else if (cm.canHold(4031849) && cm.haveItem(4031848)) {
         cm.sendNext("Now filling up the bottle with milk. The bottle is now 2/3 full of milk.")
         cm.gainItem(4031848, (short) -1)
         cm.gainItem(4031849, (short) 1)

         cm.setQuestProgress(2180, 1, 2)
      } else if (cm.canHold(4031850) && cm.haveItem(4031849)) {
         cm.sendNext("Now filling up the bottle with milk. The bottle is now completely full of milk.")
         cm.gainItem(4031849, (short) -1)
         cm.gainItem(4031850, (short) 1)

         cm.setQuestProgress(2180, 1, 2)
      } else {
         cm.sendNext("Your inventory is full, and there's no room for a milk bottle.")
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1092091 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092091(cm: cm))
   }
   return (NPC1092091) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }