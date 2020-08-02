package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120103 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] maleFace = [20000, 20016, 20019, 20020, 20021, 20024, 20026]
   int[] femaleFace = [21000, 21002, 21009, 21016, 21022, 21025, 21027]
   int[] faceNew = []
   int[] colors = []

   def start() {
      cm.sendSimple(I18nMessage.from("9120103_WELCOME"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 1) {
               beauty = 0
               faceNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleFace.length; i++) {
                     faceNew = ScriptUtils.pushItemIfTrue(faceNew, maleFace[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               } else {
                  for (int i = 0; i < femaleFace.length; i++) {
                     faceNew = ScriptUtils.pushItemIfTrue(faceNew, femaleFace[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("9120103_REGULAR_COUPON_CONFIRM"))
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
               cm.sendYesNo(I18nMessage.from("9120103_REGULAR_LENS_COUPON_CONFIRM"))
            }
         } else if (status == 2) {
            if (beauty == 0) {
               if (cm.haveItem(5152008)) {
                  cm.gainItem(5152008, (short) -1)
                  cm.setFace(faceNew[Math.floor(Math.random() * faceNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("9120103_ENJOY_NEW_FACE"))
               } else {
                  cm.sendOk(I18nMessage.from("9120103_MISSING_SURGERY_COUPON"))
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152046)) {
                  cm.gainItem(5152046, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk(I18nMessage.from("9120103_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("9120103_MISSING_SURGERY_COUPON"))
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC9120103 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120103(cm: cm))
   }
   return (NPC9120103) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }