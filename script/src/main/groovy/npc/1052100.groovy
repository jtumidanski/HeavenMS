package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052100 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30040, 30130, 30780, 30850, 30860, 30920, 33040]
   int[] femaleHair = [31090, 31140, 31330, 31440, 31760, 31880, 34050]
   int[] hairNew = []
   int[] hairColor

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
            cm.sendSimple(I18nMessage.from("1052100_HELLO"))
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < maleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i] + (cm.getPlayer().getHair() % 10).toInteger(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < femaleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[i] + (cm.getPlayer().getHair() % 10).toInteger(), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? If you have #b#t5150003##k I'll change it for you. Choose the one to your liking~.", hairNew)
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("I can totally change your hair color and make it look so good. Why don't you change it up a bit? With #b#t5151003##k I'll change it for you. Choose the one to your liking.", hairColor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5420003)) {
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("1052100_ENJOY_YOUR_NEW_HAIR_STYLE"))
               } else if (cm.haveItem(5150003)) {
                  cm.gainItem(5150003, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("1052100_ENJOY_YOUR_NEW_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1052100_MISSING_COUPON"))
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151003)) {
                  cm.gainItem(5151003, (short) -1)
                  cm.setHair(hairColor[selection])
                  cm.sendOk(I18nMessage.from("1052100_ENJOY_YOUR_NEW_HAIR_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("1052100_MISSING_COLOR_COUPON"))
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150003, (short) 1)
                  cm.sendOk(I18nMessage.from("1052100_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151003, (short) 1)
                  cm.sendOk(I18nMessage.from("1052100_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("1052100_NOT_ENOUGH_MESOS"))
               }
            }
         }
      }
   }
}

NPC1052100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052100(cm: cm))
   }
   return (NPC1052100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }