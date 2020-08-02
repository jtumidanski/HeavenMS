package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270024 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleFace = [20005, 20012, 20013, 20020, 20021, 20026]
   int[] femaleFace = [21006, 21009, 21011, 21012, 21021, 21025]
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
            cm.sendSimple(I18nMessage.from("9270024_HELLO"))
         } else if (status == 1) {
            if (!cm.haveItem(5152038)) {
               cm.sendOk(I18nMessage.from("9270024_MISSING_SURGERY_COUPON"))
               cm.dispose()
               return
            }

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
            cm.sendStyle("Let's see... I can totally transform your face into something new. Don't you want to try it? For #b#t5152038##k, you can get the face of your liking. Take your time in choosing the face of your preference...", faceNew)
         } else if (status == 2) {
            cm.gainItem(5152038, (short) -1)
            cm.setFace(faceNew[selection])
            cm.sendOk(I18nMessage.from("9270024_ENJOY_NEW_FACE"))

            cm.dispose()
         }
      }
   }
}

NPC9270024 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270024(cm: cm))
   }
   return (NPC9270024) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }