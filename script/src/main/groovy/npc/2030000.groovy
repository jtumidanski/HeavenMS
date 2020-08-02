package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Jeff
	Map(s): 		El Nath : Ice Valley II
	Description: 	
*/


class NPC2030000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031450, 1)) {
         cm.warp(921100100, 1)
         cm.dispose()
         return
      }

      cm.sendNext(I18nMessage.from("2030000_FURTHER_AND_DEEPER"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0 && cm.getLevel() > 49) {
            cm.sendNext(I18nMessage.from("2030000_IF_YOU_EVER_CHANGE_YOUR_MIND"))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (cm.getLevel() > 49) {
               cm.sendYesNo(I18nMessage.from("2030000_DO_YOU_WANT_TO_GO"))
            } else {
               cm.sendPrev(I18nMessage.from("2030000_YOU_ARE_TOO_WEAK"))
            }
         } else if (status == 2) {
            if (cm.getLevel() >= 50) {
               cm.warp(211040300, 5)
            }
            cm.dispose()
         }
      }
   }
}

NPC2030000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2030000(cm: cm))
   }
   return (NPC2030000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }