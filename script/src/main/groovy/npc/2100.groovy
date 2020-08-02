package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Sera
	Map(s): 		Maple Road : Entrance - Mushroom Town Training Camp (0), Maple Road: Upper level of the Training Camp (1), Maple Road : Entrance - Mushroom Town Training Camp (3)
	Description: 		First NPC
*/


class NPC2100 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.c.getPlayer().getMapId() == 0 || cm.c.getPlayer().getMapId() == 3) {
         cm.sendYesNo(I18nMessage.from("2100_WELCOME"))
      } else {
         cm.sendNext(I18nMessage.from("2100_FIRST_TRAINING_PROGRAM"))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && status == 0) {
            cm.sendYesNo(I18nMessage.from("2100_DO_YOU_REALLY_WANT_TO_START"))
            return
         } else if (mode == 0 && status == 1 && type == 0) {
            status -= 2
            start()
            return
         } else if (mode == 0 && status == 1 && type == 1) {
            cm.sendNext(I18nMessage.from("2100_TALK_TO_ME_AGAIN"))
         }
         cm.dispose()
         return
      }
      if (cm.c.getPlayer().getMapId() == 0 || cm.c.getPlayer().getMapId() == 3) {
         if (status == 0) {
            cm.sendNext(I18nMessage.from("2100_OK_THEN"))
         } else if (status == 1 && type == 1) {
            cm.sendNext(I18nMessage.from("2100_SKIP_TRAINING"))
         } else if (status == 1) {
            cm.warp(1, 0)
            cm.dispose()
         } else {
            cm.warp(40000, 0)
            cm.dispose()
         }
      } else if (status == 0) {
         cm.sendPrev(I18nMessage.from("2100_ENTITLED_TO_OCCUPY_A_JOB"))
      } else {
         cm.dispose()
      }
   }
}

NPC2100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100(cm: cm))
   }
   return (NPC2100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }