package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120101 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30260, 30280, 30340, 30360, 30710, 30780, 30790, 30800, 30810, 30820, 30920]
   int[] femaleHair = [31350, 31410, 31460, 31540, 31550, 31710, 31720, 31770, 31790, 31800, 31850, 34000]
   int[] hairNew = []
   int[] hairColor = []

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple(I18nMessage.from("9120101_HELLO"))
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < femaleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("9120101_REG_COUPON_INFO"))
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendYesNo(I18nMessage.from("9120101_REG_COUPON_CONFIRMATION"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150008)) {
                  cm.gainItem(5150008, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("9120101_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("9120101_MISSING_STYLE_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151008)) {
                  cm.gainItem(5151008, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("9120101_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("9120101_MISSING_COLOR_COUPON"))
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150008, (short) 1)
                  cm.sendOk(I18nMessage.from("9120101_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151008, (short) 1)
                  cm.sendOk(I18nMessage.from("9120101_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("9120101_NOT_ENOUGH_MESO"))
               }
            }
         }
      }
   }
}

NPC9120101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120101(cm: cm))
   }
   return (NPC9120101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }