package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2090104 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleFace = [20002, 20005, 20007, 20011, 20014, 20017, 20029]
   int[] femaleFace = [21001, 21010, 21013, 21018, 21020, 21021, 21030]
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
            cm.sendSimple(I18nMessage.from("2090104_HELLO"))
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
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
               cm.sendYesNo(I18nMessage.from("2090104_REGULAR_COUPON_INFO"))
            } else if (selection == 2) {
               beauty = 2
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               int[] temp = [current, current + 100, current + 300, current + 500, current + 600, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendYesNo(I18nMessage.from("2090104_REGULAR_EYE_INFO"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5152027)) {
                  cm.gainItem(5152027, (short) -1)
                  cm.setFace(faceNew[Math.floor(Math.random() * faceNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2090104_ENJOY_NEW_FACE"))
               } else {
                  cm.sendOk(I18nMessage.from("2090104_MISSING_SURGERY_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5152042)) {
                  cm.gainItem(5152042, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk(I18nMessage.from("2090104_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("2090104_MISSING_LENS_COUPON"))
               }
            }
         }
      }
   }
}

NPC2090104 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2090104(cm: cm))
   }
   return (NPC2090104) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }