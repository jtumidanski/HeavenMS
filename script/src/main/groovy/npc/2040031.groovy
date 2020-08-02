package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Document Roll
	Map(s): 		Chloe's House
	Description: 	
*/


class NPC2040031 {
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
            cm.sendOk(I18nMessage.from("2040031_ROLL_ON_THE_GROUND"))
            cm.dispose()
         }
      }
   }
}

NPC2040031 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040031(cm: cm))
   }
   return (NPC2040031) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }