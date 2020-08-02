package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201069 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] maleFace = [20000, 20001, 20003, 20004, 20005, 20006, 20008, 20012, 20031]
   int[] femaleFace = [21001, 21002, 21003, 21004, 21005, 21006, 21008, 21012, 21016]
   int[] faceNew = []

   def start() {
      cm.sendSimple(I18nMessage.from("9201069_HELLO"))
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
               cm.sendStyle("Let's see... I can totally transform your face into something new. Don't you want to try it? For #b#t5152034##k, you can get the face of your liking. Take your time in choosing the face of your preference.", faceNew)
            }
         } else if (status == 2) {
            if (cm.haveItem(5152034)) {
               cm.gainItem(5152034, (short) -1)
               cm.setFace(faceNew[selection])
               cm.sendOk(I18nMessage.from("9201069_ENJOY_NEW_FACE"))
            } else {
               cm.sendOk(I18nMessage.from("9201069_MISSING_SURGERY_COUPON"))
            }

            cm.dispose()
         }
      }
   }
}

NPC9201069 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201069(cm: cm))
   }
   return (NPC9201069) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }