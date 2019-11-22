package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2103005 {
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
            if (cm.isQuestStarted(3929)) {
               String progress = cm.getQuestProgress(3929)
               int slot = 1

               String ch = progress[slot]
               if (ch == '2') {
                  String nextProgress = progress.substring(0, slot) + '3' + progress.substring(slot + 1)

                  cm.gainItem(4031580, (short) -1)
                  cm.setQuestProgress(3929, nextProgress)
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2103005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2103005(cm: cm))
   }
   return (NPC2103005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }