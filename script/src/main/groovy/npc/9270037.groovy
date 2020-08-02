package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270037 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleHair = [30110, 30180, 30260, 30290, 30300, 30350, 30470, 30720, 30840]
   int[] femaleHair = [31110, 31200, 31250, 31280, 31600, 31640, 31670, 31810, 34020]
   int[] hairNew = []
   int[] hairColor = []


   def start() {
      cm.sendSimple(I18nMessage.from("9270037_HELLO"))
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

            cm.sendYesNo(I18nMessage.from("9270037_REG_CONFIRM"))
         } else if (selection == 2) {
            beauty = 2
            hairColor = []
            int current = (cm.getPlayer().getHair() / 10).intValue() * 10
            for (int i = 0; i < 8; i++) {
               hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
            }
            cm.sendYesNo(I18nMessage.from("9270037_REG_CONFIRM_2"))
         } else if (status == 2) {
            if (beauty == 1) {
               if (cm.haveItem(5150032)) {
                  cm.gainItem(5150032, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("9270037_ENJOY_NEW_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("9270037_MISSING_STYLE_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151027)) {
                  cm.gainItem(5151027, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("9270037_ENJOY_NEW_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("9270037_MISSING_COLOR_COUPON"))
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC9270037 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270037(cm: cm))
   }
   return (NPC9270037) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }