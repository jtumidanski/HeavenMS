package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9103000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int qty = 0

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.isEventLeader()) {
               if (!cm.getEventInstance().isEventTeamTogether()) {
                  cm.sendOk(I18nMessage.from("9103000_MEMBER_MISSING"))
                  cm.dispose()
               } else if (cm.hasItem(4001106, 30)) {
                  qty = cm.getItemQuantity(4001106)
                  cm.sendYesNo(I18nMessage.from("9103000_SPLENDID").with(qty))
               } else {
                  cm.sendOk(I18nMessage.from("9103000_NEED_30"))
                  cm.dispose()
               }
            } else {
               cm.sendOk(I18nMessage.from("9103000_PARTY_LEADER_MUST_TALK"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.removeAll(4001106)
            cm.getEventInstance().giveEventPlayersExp(50 * qty)
            cm.getEventInstance().clearPQ()
            cm.dispose()
         }
      }
   }
}

NPC9103000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9103000(cm: cm))
   }
   return (NPC9103000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }