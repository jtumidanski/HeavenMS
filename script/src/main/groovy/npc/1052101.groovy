package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052101 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30040, 30130, 30520, 30770, 30780, 30850, 30920, 33040]
   int[] femaleHair = [31060, 31140, 31330, 31440, 31520, 31750, 31760, 31880, 34050]
   int[] maleHairExperimental = [30130, 30430, 30520, 30770, 30780, 30850, 30920, 33040]
   int[] femaleHairExperimental = [31060, 31140, 31330, 31520, 31760, 31880, 34010, 34050]
   int[] hairNew = []
   int[] hairColor

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
            cm.sendSimple(I18nMessage.from("1052101_HELLO"))
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
               cm.sendYesNo(I18nMessage.from("1052101_REGULAR_INFO"))
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
               cm.sendYesNo(I18nMessage.from("1052101_EXPERIMENTAL_INFO"))
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendYesNo(I18nMessage.from("1052101_REGULAR_CONFIRMATION"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150011)) {
                  cm.gainItem(5150011, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("1052101_ENJOY_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1052101_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151002)) {
                  cm.gainItem(5151002, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("1052101_ENJOY_HAIR_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("1052101_MISSING_COLOR_COUPON"))
               }
            }
            if (beauty == 3) {
               if (cm.haveItem(5150002)) {
                  cm.gainItem(5150002, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("1052101_ENJOY_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1052101_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150011, (short) 1)
                  cm.sendOk(I18nMessage.from("1052101_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151002, (short) 1)
                  cm.sendOk(I18nMessage.from("1052101_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("1052101_NOT_ENOUGH_MESOS"))
               }
            }
         }
      }
   }
}

NPC1052101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052101(cm: cm))
   }
   return (NPC1052101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }