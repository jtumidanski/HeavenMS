package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100008 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleFace = [20000, 20004, 20005, 20012, 20013, 20031]
   int[] femaleFace = [21000, 21003, 21006, 21009, 21012, 21024]
   int[] faceNew = []
   int[] colors = []
   int current

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
            cm.sendSimple(I18nMessage.from("2100008_HELLO"))
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 0

               faceNew = []
               if (cm.getChar().getGender() == 0) {
                  for (int i = 0; i < maleFace.length; i++) {
                     faceNew = ScriptUtils.pushItemIfTrue(faceNew, maleFace[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getChar().getGender() == 1) {
                  for (int i = 0; i < femaleFace.length; i++) {
                     faceNew = ScriptUtils.pushItemIfTrue(faceNew, femaleFace[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendStyle("Hmmm... Face of beauty glows even under cover and burning desert. Choose the face you want, and I will pull out my outstanding skill for the great make over.", faceNew)
            } else if (selection == 2) {
               beauty = 1

               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               int[] temp = [current, current + 100, current + 300, current + 600, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendStyle("With the utmost finesse matching that of the sparkling sands of the desert that gleefully embraces the rooftop of the Palace, we will make your eyes shine even brighter with the new lenses. Select the one you want to use...", colors)
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
                  cm.sendOk(I18nMessage.from("2100008_MISSING_ONE_TIME_LENS_COUPON"))
                  cm.dispose()
                  return
               }

               cm.sendStyle("What kind of lens would you like to wear? Please choose the style of your liking.", colors)
            }
         } else if (status == 2) {
            cm.dispose()

            if (beauty == 0) {
               if (cm.haveItem(5152030)) {
                  cm.gainItem(5152030, (short) -1)
                  cm.setFace(faceNew[selection])
                  cm.sendOk(I18nMessage.from("2100008_ENJOY_NEW_FACE"))
               } else {
                  cm.sendNext(I18nMessage.from("2100008_MISSING_COUPON"))
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152047)) {
                  cm.gainItem(5152047, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("2100008_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("2100008_MISSING_SURGERY_COUPON"))
               }
            } else if (beauty == 3) {
               int color = (colors[selection] / 100) % 100 | 0

               if (cm.haveItem(5152100 + color)) {
                  cm.gainItem(5152100 + color, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("2100008_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("2100008_MISSING_LENS_COUPON"))
               }
            }
         }
      }
   }
}

NPC2100008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100008(cm: cm))
   }
   return (NPC2100008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }