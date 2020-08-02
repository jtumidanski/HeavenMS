package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100005 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleHair = [30150, 30170, 30180, 30320, 30330, 30410, 30460, 30680, 30800, 30820, 30900]
   int[] femaleHair = [31090, 31190, 31330, 31340, 31400, 31420, 31520, 31620, 31650, 31660, 34000]
   int[] hairNew = []
   int[] hairColor = []

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         if (type == 7) {
            cm.sendNext(I18nMessage.from("2100005_NOT_READY"))
         }

         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple(I18nMessage.from("2100005_HELLO"))
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 1
               hairNew = []
               if (cm.getChar().getGender() == 0) {
                  for (int i = 0; i < maleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i] + (cm.getChar().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getChar().getGender() == 1) {
                  for (int i = 0; i < femaleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[i] + (cm.getChar().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("2100005_REG_COUPON_INFO"))
            } else if (selection == 1) {
               beauty = 2
               hairColor = []
               int current = (cm.getChar().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendYesNo(I18nMessage.from("2100005_REG_COUPON_CONFIRM"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150026)) {
                  cm.gainItem(5150026, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2100005_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendNext(I18nMessage.from("2100005_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151021)) {
                  cm.gainItem(5151021, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("2100005_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendNext(I18nMessage.from("2100005_MISSING_COLOR_COUPON"))
               }
            }
         }
      }
   }
}

NPC2100005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100005(cm: cm))
   }
   return (NPC2100005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }