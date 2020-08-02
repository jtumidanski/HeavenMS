package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012007 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30030, 30020, 30000, 30270, 30230]
   int[] femaleHair = [31040, 31000, 31250, 31220, 31260]
   int[] maleHairRoyal = [30230, 30260, 30280, 30340, 30490, 30530, 30630, 30740]
   int[] femaleHairRoyal = [31110, 31220, 31230, 31630, 31650, 31710, 31790, 31890, 31930]
   int[] maleHairExperimental = [30230, 30280, 30340, 30490, 30530, 30740]
   int[] femaleHairExperimental = [31110, 31220, 31230, 31710, 31790, 31890, 31930]
   int[] hairNew = []
   int[] hairColor = []

   def start() {
      cm.sendSimple(I18nMessage.from("2012007_WHAT_DO_YOU_WANT_TO_DO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               beauty = 4
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               } else {
                  for (int i = 0; i < femaleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("2012007_DRT_COUPON_EXPLANATION"))
            } else if (selection == 1) {
               beauty = 3
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleHairRoyal.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHairRoyal[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               } else {
                  for (int i = 0; i < femaleHairRoyal.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHairRoyal[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("2012007_REG_COUPON_EXPLANATION"))
            } else if (selection == 2) {
               beauty = 1
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleHairExperimental.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHairExperimental[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               } else {
                  for (int i = 0; i < femaleHairExperimental.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHairExperimental[i] + (cm.getPlayer().getHair() % 10).intValue(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("2012007_EXP_COUPON_EXPLANATION"))
            } else if (selection == 3) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10) | 0
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendYesNo(I18nMessage.from("2012007_REG_COLOR_COUPON_EXPLANATION"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150013)) {
                  cm.gainItem(5150013, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2012007_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("2012007_MISSING_STYLE_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151004)) {
                  cm.gainItem(5151004, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("2012007_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("2012007_MISSING_COLOR_COUPON"))
               }
            } else if (beauty == 3) {
               if (cm.haveItem(5150004)) {
                  cm.gainItem(5150004, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2012007_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("2012007_MISSING_STYLE_COUPON"))
               }
            } else if (beauty == 4) {
               if (cm.haveItem(5154000)) {
                  cm.gainItem(5154000, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2012007_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("2012007_MISSING_STYLE_COUPON"))
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150013, (short) 1)
                  cm.sendOk(I18nMessage.from("2012007_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151004, (short) 1)
                  cm.sendOk(I18nMessage.from("2012007_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("2012007_NOT_ENOUGH_MESOS"))
               }
            }
         }
      }
   }
}

NPC2012007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012007(cm: cm))
   }
   return (NPC2012007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }