package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleFace = [20001, 20003, 20009, 20010, 20025, 20031]
   int[] femaleFace = [21002, 21009, 21011, 21013, 21016, 21029, 21030]
   int[] faceNew = []
   int[] colors = []

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         if (type == 7) {
            cm.sendNext(I18nMessage.from("2100009_TAKE_YOUR_TIME"))
         }

         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple(I18nMessage.from("2100009_HELLO"))
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
               cm.sendYesNo(I18nMessage.from("2100009_REG_COUPON_INFO"))
            } else if (selection == 2) {
               beauty = 1
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               int[] temp = [current, current + 100, current + 300, current + 600, current + 700]
               colors = ScriptUtils.pushItemsIfTrue(colors, temp, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               cm.sendYesNo(I18nMessage.from("2100009_REG_COUPON_CONFIRM"))
            }
         } else if (status == 2) {
            cm.dispose()

            if (beauty == 0) {
               if (cm.haveItem(5152029)) {
                  cm.gainItem(5152029, (short) -1)
                  cm.setFace(faceNew[Math.floor(Math.random() * faceNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("2100009_ENJOY_NEW_FACE"))
               } else {
                  cm.sendNext(I18nMessage.from("2100009_MISSING_COUPON"))
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152048)) {
                  cm.gainItem(5152048, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk(I18nMessage.from("2100009_ENJOY_NEW_LENS"))
               } else {
                  cm.sendOk(I18nMessage.from("2100009_MISSING_SURGERY_COUPON"))
               }
            }
         }
      }
   }
}

NPC2100009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100009(cm: cm))
   }
   return (NPC2100009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }