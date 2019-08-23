package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2103002 {
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
            if (cm.isQuestStarted(3923) && !cm.haveItem(4031578, 1)) {
               if (cm.canHold(4031578, 1)) {
                  cm.sendOk("You have just swiped the ring. Clear the area asap!", (byte) 2)
                  cm.gainItem(4031578, (short) 1)
               } else {
                  cm.sendOk("You don't have a ETC slot available.", (byte) 2)
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2103002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2103002(cm: cm))
   }
   return (NPC2103002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }