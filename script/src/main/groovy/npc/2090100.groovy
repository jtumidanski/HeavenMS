package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2090100 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30150, 30240, 30370, 30420, 30640, 30710, 30750, 30810]
   int[] femaleHair = [31140, 31160, 31180, 31300, 31460, 31470, 31660, 31910]
   int[] hairNew = []
   int[] hairColor = []

   def start() {
      cm.sendSimple(I18nMessage.from("2090100_WELCOME"))
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
               cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? With #b#t5150025##k, I'll take care of the rest for you. Choose the style of your liking!", hairNew)
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("I can totally change your hair color and make it look so good. Why don't you change it up a bit? With #b#t5151020##k, I'll take care of the rest. Choose the color of your liking!", hairColor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5420006)) {
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("2090100_ENJOY_NEW_STYLE"))
               } else if (cm.haveItem(5150025)) {
                  cm.gainItem(5150025, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("2090100_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("2090100_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151020)) {
                  cm.gainItem(5151020, (short) -1)
                  cm.setHair(hairColor[selection])
                  cm.sendOk(I18nMessage.from("2090100_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("2090100_MISSING_COLOR_COUPON"))
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150025, (short) 1)
                  cm.sendOk(I18nMessage.from("2090100_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151020, (short) 1)
                  cm.sendOk(I18nMessage.from("2090100_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("2090100_NOT_ENOUGH_MESOS"))
               }
            }
         }
      }
   }
}

NPC2090100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2090100(cm: cm))
   }
   return (NPC2090100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }