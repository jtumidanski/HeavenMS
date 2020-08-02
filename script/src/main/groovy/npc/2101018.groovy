package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101018 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if ((cm.getPlayer().getLevel() < 19 || cm.getPlayer().getLevel() > 30) && !cm.getPlayer().isGM()) {
         cm.sendNext(I18nMessage.from("2101018_LEVEL_RANGE"))
         cm.dispose()
         return
      }
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 4) {
         cm.getPlayer().saveLocation("MIRROR")
         cm.warp(980010000, 3)
         cm.dispose()
      }
      if (mode != 1) {
         if (mode == 0 && type == 0) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         cm.sendNext(I18nMessage.from("2101018_HUGE_FESTIVAL"))
      } else if (status == 1) {
         cm.sendNextPrev(I18nMessage.from("2101018_ARIANT_COLISEUM_CHALLENGE_EXPLAINED"))
      } else if (status == 2) {
         cm.sendSimple(I18nMessage.from("2101018_ARE_YOU_INTERESTED"))
      } else if (status == 3) {
         cm.sendNext(I18nMessage.from("2101018_EMERGE_VICTORIOUS"))
      }
   }
}

NPC2101018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101018(cm: cm))
   }
   return (NPC2101018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }