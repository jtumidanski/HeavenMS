package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2083005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestStarted(6280)) {
         if (cm.hasItem(4031454)) {
            cm.sendOk(I18nMessage.from("2083005_POURED_WATER"))
            cm.gainItem(4031454, (short) -1)
            cm.gainItem(4031455, (short) 1)
         }
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2083005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2083005(cm: cm))
   }
   return (NPC2083005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }