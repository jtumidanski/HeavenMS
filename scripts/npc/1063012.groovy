package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1063012 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if(cm.isQuestStarted(2236) && cm.haveItem(4032263, 1)) {
         int progress = cm.getQuestProgress(2236)
         int map = cm.getMapId()

         if(map == 105050200) activateShamanRock(0,progress)
         else if(map == 105060000) activateShamanRock(1,progress)
         else if(map == 105070000) activateShamanRock(2,progress)

         else if(map == 105090000) { // workaround... TWO SAME NPC ID ON SAME MAP
            if(!activateShamanRock(3,progress)) {
               activateShamanRock(4,progress)
            }
         }

         else if(map == 105090100) activateShamanRock(5,progress)
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }

   def activateShamanRock(int slot, int progress) {
      int active = (progress >> slot) % 2
      if(!active) {
         progress |= (1 << slot)

         cm.updateQuest(2236, progress)
         cm.gainItem(4032263, (short) -1)
         cm.sendOk("The seal took it's place, repelling the evil in the area.")
         return 1
      }

      return 0
   }
}

NPC1063012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1063012(cm: cm))
   }
   return (NPC1063012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }