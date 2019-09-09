package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111014 {
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
            if (cm.isQuestStarted(3311)) {
               cm.setQuestProgress(3311, 1, 1)
               cm.sendOk("The diary of Dr. De Lang. A lot of formulas and pompous scientific texts can be found all way through the pages, but it is worth noting that in the last entry (3 weeks ago), it is written that he concluded the researches on an improvement on the blueprints for the Neo Huroids, thus making the last preparations to show it to the 'society'... No words after this...", (byte) 2)
            } else if (cm.isQuestStarted(3322) && !cm.haveItem(4031697, 1)) {
               if (cm.canHold(4031697, 1)) {
                  cm.gainItem(4031697, (short) 1)
               } else {
                  cm.sendNext("Your inventory is full, make sure a ETC slot is available for the item.")
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2111014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111014(cm: cm))
   }
   return (NPC2111014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }