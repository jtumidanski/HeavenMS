package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111023 {
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
            if (cm.isQuestStarted(3345)) {
               int progress = cm.getQuestProgressInt(3345)

               if (progress == 3 && cm.haveItem(4031739, 1) && cm.haveItem(4031740, 1) && cm.haveItem(4031741, 1)) {
                  cm.setQuestProgress(3345, 4)
                  cm.gainItem(4031739, (short) -1)
                  cm.gainItem(4031740, (short) -1)
                  cm.gainItem(4031741, (short) -1)

                  cm.sendOk("(As you place the shards a light shines over the circle, repelling whatever omens were brewing inside the artifact.)", (byte) 2)
                  cm.dispose()
               } else if (progress < 4) {
                  cm.setQuestProgress(3345, 0)
                  cm.dispose()
               } else {
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC2111023 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111023(cm: cm))
   }
   return (NPC2111023) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }