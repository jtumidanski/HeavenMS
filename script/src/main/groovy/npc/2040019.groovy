package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040019 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] maleFace = [20001, 20003, 20007, 20013, 20021, 20023, 20025]
   int[] femaleFace = [21002, 21004, 21006, 21008, 21022, 21027, 21029]
   int[] faceNew = []

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
            cm.sendSimple(I18nMessage.from("2040019_RANDOM_SURGERY"))
         } else if (status == 1) {
            if (selection == 2) {
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
               cm.sendYesNo(I18nMessage.from("2040019_REGULAR_COUPON_INFO"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (cm.haveItem(5152006)) {
               cm.gainItem(5152006, (short) -1)
               cm.setFace(faceNew[Math.floor(Math.random() * faceNew.length).intValue()])
               cm.sendOk(I18nMessage.from("2040019_ENJOY"))
            } else {
               cm.sendOk(I18nMessage.from("2040019_MISSING_SURGERY_COUPON"))
            }
         }
      }
   }
}

NPC2040019 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040019(cm: cm))
   }
   return (NPC2040019) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }