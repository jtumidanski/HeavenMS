package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2082002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (status == 0) {
         cm.sendYesNo(I18nMessage.from("2082002_LEAVE_THE_FLIGHT"))
         status++
      } else {
         if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
            cm.dispose()
         } else {
            if (status == 1) {
               cm.sendNext(I18nMessage.from("2082002_SEE_YOU_NEXT_TIME"))
               status++
            } else if (status == 2) {
               cm.warp(240000110, 0)// back to Leafre
               cm.dispose()
            }
         }
      }
   }
}

NPC2082002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2082002(cm: cm))
   }
   return (NPC2082002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }