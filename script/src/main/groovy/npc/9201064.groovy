package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201064 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30250, 30490, 30730, 30870, 30880, 33100]
   int[] femaleHair = [31320, 31450, 31560, 31730, 31830]
   int[] hairNew = []
   int[] hairColor = []

   def start() {
      cm.sendSimple(I18nMessage.from("9201064_HELLO"))
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
         if (status == 1) {
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
               cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? With #b#t5150031##k, I'll take care of the rest for you. Choose the style of your liking!", hairNew)
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("I can totally change your hair color and make it look so good. Why don't you change it up a bit? With #b#t5151026##k, I'll take care of the rest. Choose the color of your liking!", hairColor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5420001)) {
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("9201064_ENJOY_NEW_STYLE"))
               } else if (cm.haveItem(5150031)) {
                  cm.gainItem(5150031, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("9201064_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("9201064_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151026)) {
                  cm.gainItem(5151026, (short) -1)
                  cm.setHair(hairColor[selection])
                  cm.sendOk(I18nMessage.from("9201064_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("9201064_MISSING_COLOR_COUPON"))
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150031, (short) 1)
                  cm.sendOk(I18nMessage.from("9201064_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151026, (short) 1)
                  cm.sendOk(I18nMessage.from("9201064_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("9201064_NOT_ENOUGH_MESOS"))
               }
            }
         }
      }
   }
}

NPC9201064 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201064(cm: cm))
   }
   return (NPC9201064) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }