package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30190, 30220, 30250, 30540, 30610, 30620, 30640, 30650, 30660, 30840, 30870, 30940, 30990]
   int[] femaleHair = [31170, 31270, 31290, 31510, 31540, 31550, 31600, 31640, 31680, 31810, 31830, 31840, 31870]
   int[] maleHairExperimental = [30030, 30190, 30220, 30250, 30540, 30610, 30620, 30640, 30650, 30660, 30840, 30990]
   int[] femaleHairExperimental = [31170, 31270, 31430, 31510, 31540, 31550, 31600, 31680, 31810, 31830, 31840, 31870]
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
            cm.sendSimple(I18nMessage.from("2041009_ASSISTANT"))
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 3
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
               cm.sendYesNo(I18nMessage.from("2041009_REGULAR_COUPON_INFO"))
            } else if (selection == 1) {
               beauty = 1
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleHairExperimental.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHairExperimental[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < femaleHairExperimental.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHairExperimental[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("2041009_EXP_COUPON_INFO"))
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendYesNo(I18nMessage.from("2041009_REGULAR_CONFIRMATION"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150012)) {
                  cm.gainItem(5150012, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2041009_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("2041009_MISSING_STYLE_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151006)) {
                  cm.gainItem(5151006, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("2041009_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("2041009_MISSING_COLOR_COUPON"))
               }
            } else if (beauty == 3) {
               if (cm.haveItem(5150006)) {
                  cm.gainItem(5150006, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("2041009_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("2041009_MISSING_COLOR_COUPON"))
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150012, (short) 1)
                  cm.sendOk(I18nMessage.from("2041009_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151006, (short) 1)
                  cm.sendOk(I18nMessage.from("2041009_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("2041009_NOT_ENOUGH_MESOS"))
               }
            }
         }
      }
   }
}

NPC2041009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041009(cm: cm))
   }
   return (NPC2041009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }