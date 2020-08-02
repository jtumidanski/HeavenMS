package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201070 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] maleFace = [20001, 20008, 20011, 20013, 20024, 20029, 20032]
   int[] femaleFace = [21000, 21007, 21011, 21012, 21017, 21020, 21022]
   int[] faceNew = []

   def start() {
      cm.sendSimple(I18nMessage.from("9201070_HELLO"))
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
         if (status == 1) {
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
               cm.sendYesNo(I18nMessage.from("9201070_REG_CONFIRM"))
            }
         } else if (status == 2) {
            if (cm.haveItem(5152033)) {
               cm.gainItem(5152033, (short) -1)
               cm.setFace(faceNew[Math.floor(Math.random() * faceNew.length).intValue()])
               cm.sendOk(I18nMessage.from("9201070_ENJOY_NEW_FACE"))
            } else {
               cm.sendOk(I18nMessage.from("9201070_MISSING_SURGERY_COUPON"))
            }

            cm.dispose()
         }
      }
   }
}

NPC9201070 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201070(cm: cm))
   }
   return (NPC9201070) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }