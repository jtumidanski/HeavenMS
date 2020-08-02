package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Neru
	Map(s): 		Ludibrium : Ludibrium Pet Walkway
	Description: 	
*/


class NPC2040033 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031128)) {
         cm.sendNext(I18nMessage.from("2040033_BROTHERS_LETTER"))
      } else {
         cm.sendOk(I18nMessage.from("2040033_CHILL_FOR_A_FEW"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode >= 1) {
         if (cm.getPlayer().getNoPets() == 0) {
            cm.sendNextPrev(I18nMessage.from("2040033_GET_OUTTA_HERE"))
         } else {
            cm.gainItem(4031128, (short) -1)
            cm.gainCloseness(4)
            cm.sendNextPrev(I18nMessage.from("2040033_TRAIN_YOUR_PET_AGAIN"))
         }
      }
      cm.dispose()
   }
}

NPC2040033 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040033(cm: cm))
   }
   return (NPC2040033) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }