package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

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
         cm.sendYesNo(I18nMessage.from("2041001_DO_YOU_WISH_TO_LEAVE"))

         status++
      } else {
         if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
            cm.dispose()
         } else {
            if (status == 1) {
               cm.sendNext(I18nMessage.from("2041001_SEE_YOU_NEXT_TIME"))

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