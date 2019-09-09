package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201128 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map = 677000004
   int quest = 28179
   int questItem = 4032491

   def start() {
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
         if (cm.isQuestStarted(quest)) {
            if (cm.haveItem(questItem)) {
               cm.sendYesNo("Would you like to move to #b#m" + map + "##k?")
            } else {
               cm.sendOk("The entrance is blocked by a force that can only be lifted by those holding an emblem.")
               cm.dispose()
            }
         } else {
            cm.sendOk("The entrance is blocked by a strange force.")
            cm.dispose()
         }
      } else {
         if (cm.haveItem(4001341, 1)) {
            cm.gainItem(4001341, (short) -1)
         }
         if (cm.haveItem(4032478, 1)) {
            cm.gainItem(4032478, (short) -1)
         }

         cm.warp(map, 0)
         cm.dispose()
      }
   }
}

NPC9201128 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201128(cm: cm))
   }
   return (NPC9201128) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }