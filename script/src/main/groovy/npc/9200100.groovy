package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9200100 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int regularPrice = 1000000
   int vipPrice = 1000000
   int[] colors = []

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
            cm.sendSimple(I18nMessage.from("9200100_HELLO"))
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               int[] temp = [current, current + 100, current + 200, current + 400, current + 600, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendYesNo(I18nMessage.from("9200100_REG_COUPON_CONFIRM"))
            } else if (selection == 2) {
               beauty = 2
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               int[] temp = [current, current + 100, current + 200, current + 400, current + 600, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of lens would you like to wear? Choose the style of your liking.", colors)
            } else if (selection == 3) {
               beauty = 3
               int current = 0
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
                  cm.sendOk(I18nMessage.from("9200100_NO_ONE_TIME_COSMETIC_LENS"))
                  cm.dispose()
                  return
               }

               cm.sendStyle("What kind of lens would you like to wear? Please choose the style of your liking.", colors)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5152010)) {
                  cm.gainItem(5152010, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk(I18nMessage.from("9200100_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9200100_MISSING_LENS_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5152013)) {
                  cm.gainItem(5152013, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("9200100_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9200100_MISSING_LENS_COUPON"))
               }
            } else if (beauty == 3) {
               int color = (colors[selection] / 100) % 100 | 0

               if (cm.haveItem(5152100 + color)) {
                  cm.gainItem(5152100 + color, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("9200100_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9200100_MISSING_LENS_COUPON"))
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= regularPrice) {
                  cm.gainMeso(-regularPrice)
                  cm.gainItem(5152010, (short) 1)
                  cm.sendOk(I18nMessage.from("9200100_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= vipPrice) {
                  cm.gainMeso(-vipPrice)
                  cm.gainItem(5152013, (short) 1)
                  cm.sendOk(I18nMessage.from("9200100_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("9200100_NOT_ENOUGH_MESO"))
               }
            }
         }
      }
   }
}

NPC9200100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9200100(cm: cm))
   }
   return (NPC9200100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }