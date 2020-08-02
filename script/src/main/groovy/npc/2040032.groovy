package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Weaver
	Map(s): 		Ludibrium : Ludibrium Pet Walkway
	Description: 	
*/


class NPC2040032 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo(I18nMessage.from("2040032_WALK_WITH_YOUR_PET"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         cm.sendNext(I18nMessage.from("2040032_TOO_BUSY"))
      } else if (mode == 1) {
         if (cm.haveItem(4031128)) {
            cm.sendNext(I18nMessage.from("2040032_GET_THE_LETTER"))
         } else {
            cm.gainItem(4031128, (short) 1)
            cm.sendOk(I18nMessage.from("2040032_HERE_IS_THE_LETTER"))
         }
      }
      cm.dispose()
   }
}

NPC2040032 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040032(cm: cm))
   }
   return (NPC2040032) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }