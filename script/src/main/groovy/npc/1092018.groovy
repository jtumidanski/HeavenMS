package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092018 {
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
            if ((!cm.isQuestCompleted(2162)) && !cm.haveItem(4031839, 1)) {
               if (cm.canHold(4031839, 1)) {
                  cm.gainItem(4031839, (short) 1)
                  cm.sendNext("(You retrieved a Crumpled Paper standing out of the trash can. It's content seems important.)", (byte) 2)
               } else {
                  cm.sendNext("(You see a Crumpled Paper standing out of the trash can. It's content seems important, but you can't retrieve it since your inventory is full.)", (byte) 2)
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC1092018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092018(cm: cm))
   }
   return (NPC1092018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }