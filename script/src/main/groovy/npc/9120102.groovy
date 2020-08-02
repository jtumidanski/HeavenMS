package npc
import tools.I18nMessage


import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120102 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] maleFace = [20000, 20004, 20005, 20012, 20020, 20031]
   int[] femaleFace = [21000, 21003, 21006, 21012, 21021, 21024]
   int[] faceNew = []
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
            cm.sendSimple(I18nMessage.from("9120102_WELCOME"))

         } else if (status == 1) {
            if (selection == 1) {
               beauty = 0
               faceNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleFace.length; i++) {
                     faceNew = ScriptUtils.pushItemIfTrue(faceNew, maleFace[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < femaleFace.length; i++) {
                     faceNew = ScriptUtils.pushItemIfTrue(faceNew, femaleFace[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendStyle("I can totally transform your face into something new... how about giving us a try? For #b#t5152009##k, you can get the face of your liking...take your time in choosing the face of your preference.", faceNew)
            } else if (selection == 2) {
               beauty = 1
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               int[] temp = [current, current + 100, current + 200, current + 300, current + 400, current + 500, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendStyle("With our new computer program, you can see yourself after the treatment in advance. What kind of lens would you like to wear? Please choose the style of your liking.", colors)
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
                  cm.sendOk(I18nMessage.from("9120102_NO_ONE_TIME_COSMETIC_LENS"))

                  cm.dispose()
                  return
               }

               cm.sendStyle("What kind of lens would you like to wear? Please choose the style of your liking.", colors)
            }
         } else if (status == 2) {
            cm.dispose()

            if (beauty == 0) {
               if (cm.haveItem(5152009)) {
                  cm.gainItem(5152009, (short) -1)
                  cm.setFace(faceNew[selection])
                  cm.sendOk(I18nMessage.from("9120102_ENJOY_NEW_FACE"))

               } else {
                  cm.sendOk(I18nMessage.from("9120102_MISSING_SURGERY_COUPON"))

               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152045)) {
                  cm.gainItem(5152045, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("9120102_ENJOY_NEW_LENS"))

               } else {
                  cm.sendOk(I18nMessage.from("9120102_MISSING_SURGERY_COUPON"))

               }
            } else if (beauty == 3) {
               int color = (colors[selection] / 100) % 100 | 0

               if (cm.haveItem(5152100 + color)) {
                  cm.gainItem(5152100 + color, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk(I18nMessage.from("9120102_ENJOY_NEW_LENS"))

               } else {
                  cm.sendOk(I18nMessage.from("9120102_MISSING_LENS_COUPON"))

               }
            }
         }
      }
   }
}

NPC9120102 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120102(cm: cm))
   }
   return (NPC9120102) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }