package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270026 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int current
   int[] colors = []

   def start() {
      cm.sendSimple(I18nMessage.from("9270026_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 1) {
               beauty = 1
               current = cm.getPlayer().getFace() % 100 + 20000 + cm.getPlayer().getGender() * 1000
               cm.sendYesNo(I18nMessage.from("9270026_REG_CONFIRM"))
            } else if (selection == 2) {
               beauty = 2
               current = cm.getPlayer().getFace() % 100 + 20000 + cm.getPlayer().getGender() * 1000
               int[] temp = [current + 200, current + 300, current + 400, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of lens would you like to wear? Choose the style of your liking.", colors)
            } else if (selection == 3) {
               beauty = 3
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }

               colors = []
               for (int i = 0; i < 8; i++) {
                  if (cm.haveItem(5152100 + i)) {
                     colors = ScriptUtils.pushItemIfTrue(colors, current + 100 * i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }

               if (colors.length == 0) {
                  cm.sendOk(I18nMessage.from("9270026_NO_ONE_TIME_COSMETIC_LENS"))
                  cm.dispose()
                  return
               }

               cm.sendStyle("What kind of lens would you like to wear? Please choose the style of your liking.", colors)
            }
         } else if (status == 2) {
            if (beauty == 1) {
               if (cm.haveItem(5152039)) {
                  cm.gainItem(5152039, (short) -1)
                  cm.setFace(Math.floor(Math.random() * 8).intValue() * 100 + current)
                  cm.sendOk(I18nMessage.from("9270026_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9270026_MISSING_LENS_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5152040)) {
                  cm.gainItem(5152040, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("9270026_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9270026_MISSING_LENS_COUPON"))
               }
            } else if (beauty == 3) {
               int color = (colors[selection] / 100) % 100 | 0

               if (cm.haveItem(5152100 + color)) {
                  cm.gainItem(5152100 + color, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("9270026_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9270026_MISSING_LENS_COUPON"))
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC9270026 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270026(cm: cm))
   }
   return (NPC9270026) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }