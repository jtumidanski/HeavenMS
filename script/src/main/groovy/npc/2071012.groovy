package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2071012 {
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
            if (cm.getQuestProgressInt(23647, 1) != 0) {
               cm.dispose()
               return
            }

            if (!cm.haveItem(4031793, 1)) {
               cm.sendOk(I18nMessage.from("2071012_I_LOST_IN_THE_WOODS"))
               cm.dispose()
               return
            }

            cm.sendYesNo(I18nMessage.from("2071012_I_LOST_IN_THE_WOODS_LONG"))
         } else if (status == 1) {
            cm.sendNext(I18nMessage.from("2071012_REWARD"))
            cm.gainItem(4031793, (short) -1)
            cm.gainFame(-5)
            cm.setQuestProgress(23647, 1, 1)

            cm.dispose()
         }
      }
   }
}

NPC2071012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2071012(cm: cm))
   }
   return (NPC2071012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }