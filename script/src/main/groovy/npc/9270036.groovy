package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270036 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleHair = [30000, 30020, 30110, 30120, 30270, 30290, 30310, 30670, 30840]
   int[] femaleHair = [31010, 31050, 31110, 31120, 31240, 31250, 31280, 31670, 31810]
   int[] hairNew = []
   int[] hairColor = []

   def start() {
      cm.sendSimple(I18nMessage.from("9270036_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (selection == 1) {
            beauty = 1
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
            cm.sendStyle("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150033##k, I'll take care of the rest for you. Choose the style of your liking!", hairNew)
         } else if (selection == 2) {
            beauty = 2
            hairColor = []
            int current = (cm.getPlayer().getHair() / 10).intValue() * 10
            for (int i = 0; i < 8; i++) {
               hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
            }
            cm.sendStyle("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151028##k, I'll take care of the rest. Choose the color of your liking!", hairColor)
         } else if (status == 2) {
            if (beauty == 1) {
               if (cm.haveItem(5150033)) {
                  cm.gainItem(5150033, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("9270036_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("9270036_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151028)) {
                  cm.gainItem(5151028, (short) -1)
                  cm.setHair(hairColor[selection])
                  cm.sendOk(I18nMessage.from("9270036_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("9270036_MISSING_COLOR_COUPON"))
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC9270036 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270036(cm: cm))
   }
   return (NPC9270036) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }