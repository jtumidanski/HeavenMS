package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2050019 {
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
            if (cm.isQuestStarted(3421)) {
               int meteoriteId = cm.getNpc() - 2050014

               int progress = cm.getQuestProgress(3421, 0)
               if ((progress >> meteoriteId) % 2 == 0 || (progress == 63 && !cm.haveItem(4031117, 6))) {
                  if (cm.canHold(4031117, 1)) {
                     progress |= (1 << meteoriteId)

                     cm.gainItem(4031117, (short) 1)
                     cm.setQuestProgress(3421, 0, progress)
                  } else {
                     cm.getPlayer().dropMessage(1, "Have a ETC slot available for this item.")
                  }
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2050019 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2050019(cm: cm))
   }
   return (NPC2050019) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }