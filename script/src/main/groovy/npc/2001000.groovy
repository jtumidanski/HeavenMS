package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Cliff
	Map(s): 		Happy Ville
	Description: 	
*/


class NPC2001000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         if (status > 0) {
            status--
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         cm.sendNext(I18nMessage.from("2001000_TALK_TO_ONE_OF_THEM"))
      } else if (status == 1) {
         cm.sendNextPrev(I18nMessage.from("2001000_TREE_RULES"))
      } else if (status == 2) {
         cm.sendNextPrev(I18nMessage.from("2001000_ITEMS_WILL_NOT_DISAPPEAR"))
      } else if (status == 3) {
         cm.sendPrev(I18nMessage.from("2001000_BUY_ORNAMENTS"))
         cm.dispose()
      }
   }
}

NPC2001000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2001000(cm: cm))
   }
   return (NPC2001000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }