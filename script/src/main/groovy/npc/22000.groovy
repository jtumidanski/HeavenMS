package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Shanks
	Map(s): 		Maple Road : Southperry (60000)
	Description: 		Brings you to Victoria Island
*/


class NPC22000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendYesNo(I18nMessage.from("22000_TAKE_THIS_SHIP"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type != 1) {
            status -= 2
         } else if (type == 1 || (mode == -1 && type != 1)) {
            if (mode == 0) {
               cm.sendOk(I18nMessage.from("22000_THINGS_STILL_TO_DO"))
            }
            cm.dispose()
            return
         }
      }
      if (status == 1) {
         if (cm.haveItem(4031801)) {
            cm.sendNext(I18nMessage.from("22000_GIVE_ME_150_MESOS"))
         } else {
            cm.sendNext(I18nMessage.from("22000_BORED_OF_THIS_PLACE"))
         }
      } else if (status == 2) {
         if (cm.haveItem(4031801)) {
            cm.sendNextPrev(I18nMessage.from("22000_RECOMMENDATION_LETTER"))
         } else if (cm.getLevel() > 6) {
            if (cm.getMeso() < 150) {
               cm.sendOk(I18nMessage.from("22000_NEED_MONEY"))
               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("22000_AWESOME"))
            }
         } else {
            cm.sendOk(I18nMessage.from("22000_NOT_STRONG_ENOUGH"))
            cm.dispose()
         }
      } else if (status == 3) {
         if (cm.haveItem(4031801)) {
            cm.gainItem(4031801, (short) -1)
         } else {
            cm.gainMeso(-150)
         }
         cm.warp(104000000, 0)
         cm.dispose()
      }
   }
}

NPC22000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC22000(cm: cm))
   }
   return (NPC22000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }