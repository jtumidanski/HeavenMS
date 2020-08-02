package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052004 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] maleFace = [20000, 20001, 20003, 20004, 20005, 20006, 20007, 20008, 20012, 20014, 20015, 20022, 20028, 20031]
   int[] femaleFace = [21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21012, 21013, 21014, 21023, 21026]
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
            cm.sendSimple(I18nMessage.from("1052004_HELLO"))
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
               cm.sendStyle(I18nMessage.from("1052004_DO_YOU_WANT_TO_TRY"), faceNew)
            }
         } else if (status == 2) {
            cm.dispose()
            if (cm.haveItem(5152001)) {
               cm.gainItem(5152001, (short) -1)
               cm.setFace(faceNew[selection])
               cm.sendOk(I18nMessage.from("1052004_ENJOY_YOUR_NEW_FACE"))
            } else {
               cm.sendOk(I18nMessage.from("1052004_MISSING_COUPON"))
            }
         }
      }
   }
}

NPC1052004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052004(cm: cm))
   }
   return (NPC1052004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }