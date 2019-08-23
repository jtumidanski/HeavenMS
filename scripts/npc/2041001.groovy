package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (status == 0) {
         cm.sendYesNo("Do you wish to leave the train?")
         status++
      } else {
         if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
            cm.dispose()
         } else {
            if (status == 1) {
               cm.sendNext("Alright, see you next time. Take care.")
               status++
            } else if (status == 2) {
               if (cm.getPlayer().getMapId() == 200000122) {
                  cm.warp(200000121, 0)
               }// back to orbis
               else {
                  cm.warp(220000110, 0)
               }// back to Ludi
               cm.dispose()
            }
         }
      }
   }
}

NPC2041001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041001(cm: cm))
   }
   return (NPC2041001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }