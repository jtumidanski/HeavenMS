package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2133002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         if (status == 0) {
            cm.dispose()
         }
         status--
      }
      if (status == 0) {
         cm.sendYesNo(I18nMessage.from("2133002_WOULD_YOU_LIKE_TO_EXIT"))
      } else if (status == 1) {
         cm.removeAll(4001163)
         cm.removeAll(4001169)
         cm.removeAll(2270004)
         cm.warp(930000800, 0)
         cm.dispose()
      }
   }
}

NPC2133002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2133002(cm: cm))
   }
   return (NPC2133002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }