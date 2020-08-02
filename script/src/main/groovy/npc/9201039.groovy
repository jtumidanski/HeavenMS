package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201039 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] maleHair = [30270, 30240, 30020, 30000, 30132, 30192, 30032, 30112, 30162]
   int[] femaleHair = [31150, 31250, 31310, 31050, 31050, 31030, 31070, 31091, 31001]
   int[] hairNew = []

   def start() {
      if (cm.isQuestCompleted(8860) && !cm.haveItem(4031528)) {
         cm.sendNext(I18nMessage.from("9201039_SNAG_A_EXP_COUPON"))
         cm.dispose()
      } else {
         cm.sendYesNo(I18nMessage.from("9201039_READY_FOR"))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         if (type == 7) {
            cm.sendNext(I18nMessage.from("9201039_GIVE_YOU_A_MINUTE"))
         }

         cm.dispose()
      }
      status++
      if (status == 1) {
         hairNew = []
         if (cm.getPlayer().getGender() == 0) {
            for (int i = 0; i < maleHair.length; i++) {
               hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i], { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
            }
         } else {
            for (int j = 0; j < femaleHair.length; j++) {
               hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[j], { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
            }
         }
         cm.sendNext(I18nMessage.from("9201039_HERE_WE_GO"))
      } else {
         if (cm.haveItem(4031528)) {
            cm.gainItem(4031528, (short) -1)
            cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
            cm.sendNextPrev(I18nMessage.from("9201039_NOT_BAD"))
            cm.dispose()
         } else {
            cm.sendNext(I18nMessage.from("9201039_NEED_COUPON"))
            cm.dispose()
         }
      }
   }
}

NPC9201039 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201039(cm: cm))
   }
   return (NPC9201039) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }