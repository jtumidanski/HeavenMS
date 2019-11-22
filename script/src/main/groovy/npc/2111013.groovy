package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111013 {
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
               int progress = cm.getQuestProgressInt(3311)

               if (progress == 4) {
                  progress = 7
               } else {
                  progress = 5
               }

               cm.setQuestProgress(3311, progress)
               cm.sendOk("This is a mug picture of Dr. De Lang. It seems he is adorning a locket with the emblem of the Alcadno academy, he is a retainer of the Alcadno society.", (byte) 2)
            }

            cm.dispose()
         }
      }
   }
}

NPC2111013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111013(cm: cm))
   }
   return (NPC2111013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }