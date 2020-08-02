package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Spiruna
	Map(s): 		Orbis : Old Man's House
	Description: 	Refining NPC
*/


class NPC2032001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      if (cm.isQuestCompleted(3034)) {
         cm.sendYesNo(I18nMessage.from("2032001_CAN_REFINE_DARK_CRYSTAL"))
      } else {
         cm.sendOk(I18nMessage.from("2032001_GO_AWAY"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
         return
      }
      status++
      if (status == 1) {
         cm.sendGetNumber("Okay, so how many do you want me to make?", 1, 1, 100)
      } else if (status == 2) {
         boolean complete = true

         if (cm.getMeso() < 500000 * selection) {
            cm.sendOk(I18nMessage.from("2032001_NOT_FOR_FREE"))
            cm.dispose()
            return
         } else if (!cm.haveItem(4004004, 10 * selection)) {
            complete = false
         } else if (!cm.canHold(4005004, selection)) {
            cm.sendOk(I18nMessage.from("2032001_NEED_EMPTY_SLOTS"))
            cm.dispose()
            return
         }
         if (!complete) {
            cm.sendOk(I18nMessage.from("2032001_NEED_ORE"))
         } else {
            cm.gainItem(4004004, (short) (-10 * selection))
            cm.gainMeso(-500000 * selection)
            cm.gainItem(4005004, (short) selection)
            cm.sendOk(I18nMessage.from("2032001_SUCCESS"))
         }
         cm.dispose()
      }
   }
}

NPC2032001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2032001(cm: cm))
   }
   return (NPC2032001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }