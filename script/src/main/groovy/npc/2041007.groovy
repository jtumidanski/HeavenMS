package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041007 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30160, 30190, 30250, 30640, 30660, 30840, 30870, 30990]
   int[] femaleHair = [31270, 31290, 31550, 31680, 31810, 31830, 31840, 31870]
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
            cm.sendSimple(I18nMessage.from("2041007_WELCOME"))

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
               cm.sendStyle("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150007##k, I'll take care of the rest for you. Choose the style of your liking!", hairNew)
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("I can completely change the color of your hair. Aren't you ready for a change? With #b#t5151007##k, I'll take care of the rest. Choose the color of your liking!", hairColor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5420005)) {
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("2041007_ENJOY_NEW_STYLE"))

               } else if (cm.haveItem(5150007)) {
                  cm.gainItem(5150007, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("2041007_ENJOY_NEW_STYLE"))

               } else {
                  cm.sendOk(I18nMessage.from("2041007_MISSING_STYLE_COUPON"))

               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151007)) {
                  cm.gainItem(5151007, (short) -1)
                  cm.setHair(hairColor[selection])
                  cm.sendOk(I18nMessage.from("2041007_ENJOY_NEW_COLOR"))

               } else {
                  cm.sendOk(I18nMessage.from("2041007_MISSING_COLOR_COUPON"))

               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150007, (short) 1)
                  cm.sendOk(I18nMessage.from("2041007_ENJOY"))

               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151007, (short) 1)
                  cm.sendOk(I18nMessage.from("2041007_ENJOY"))

               } else {
                  cm.sendOk(I18nMessage.from("2041007_NOT_ENOUGH_MESOS"))

               }
            }
         }
      }
   }
}

NPC2041007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041007(cm: cm))
   }
   return (NPC2041007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }