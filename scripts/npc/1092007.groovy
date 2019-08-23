package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Muirhat
	Map(s): 		Nautilus' Port
	Description: 	When on the quest, he warps player to Black Magician's Disciple
*/


class NPC1092007 {
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
            if (cm.getQuestStatus(2175) == 1) {
               if (cm.getPlayer().canHold(2030019)) {
                  cm.sendOk("Please take this #b#t2030019##k, it will make your life a lot easier.  #i2030019#")
               } else {
                  cm.sendOk("No free inventory spot available. Please make room in your USE inventory first.")
                  cm.dispose()
               }
            } else {
               cm.sendOk("The Black Magician and his followers. Kyrin and the Crew of Nautilus. \n They'll be chasing one another until one of them doesn't exist, that's for sure.")
               cm.dispose()
            }
         } else if (status == 1) {
            cm.gainItem(2030019, (short) 1)
            cm.warp(100000006, 0)
            cm.dispose()
         }
      }
   }
}

NPC1092007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092007(cm: cm))
   }
   return (NPC1092007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }