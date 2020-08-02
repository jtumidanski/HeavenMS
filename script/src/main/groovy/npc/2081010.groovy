package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081010 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int exitMap = 240010400

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.dispose()
         return
      }

      status++
      if (status == 0) {
         cm.sendYesNo(I18nMessage.from("2081010_DO_YOU_WANT_TO_EXIT"))
      } else if (status == 1) {
         cm.warp(exitMap, "st00")
         cm.dispose()
      }
   }
}

NPC2081010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081010(cm: cm))
   }
   return (NPC2081010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }