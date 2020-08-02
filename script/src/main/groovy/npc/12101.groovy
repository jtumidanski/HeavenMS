package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Rain
	Map(s): 		Maple Road : Amherst (1010000)
	Description: 		Talks about Amherst
*/


class NPC12101 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("12101_TOWN_CALLED_AMHERST"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && status == 2) {
            status -= 2
            start()
         } else if (mode == 0) {
            status -= 3
            action((byte) 1, type, selection)
         } else {
            cm.dispose()
         }
      } else {
         if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("12101_GO_TO_SOUTHPERRY"))
         } else if (status == 2) {
            cm.sendPrev(I18nMessage.from("12101_YOU_CAN_CHOOSE_YOUR_JOB"))
         } else if (status == 3) {
            cm.dispose()
         }
      }
   }
}

NPC12101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC12101(cm: cm))
   }
   return (NPC12101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }