package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092094 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031847)) {
         cm.sendNext(I18nMessage.from("1092094_HUNGRY_CALF_REMAINS_EMPTY"))
      } else if (cm.haveItem(4031848) || cm.haveItem(4031849) || cm.haveItem(4031850)) {
         cm.sendNext(I18nMessage.from("1092094_HUNGRY_CALF_IS_NOW_EMPTY"))
         if (cm.haveItem(4031848)) {
            cm.gainItem(4031848, (short) -1)
         } else if (cm.haveItem(4031849)) {
            cm.gainItem(4031849, (short) -1)
         } else {
            cm.gainItem(4031850, (short) -1)
         }
         cm.gainItem(4031847, (short) 1)
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         status--
         start()
      } else {
         status++
      }
      if (status == 0) {
         cm.sendPrev(I18nMessage.from("1092094_EMPTY_BOTTLE"))
      } else if (status == 1) {
         cm.dispose()
      }
   }
}

NPC1092094 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092094(cm: cm))
   }
   return (NPC1092094) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }