package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Brittany
	Map(s): 		Henesys Random Hair/Hair Color Change
	Description: 	
*/


class NPC1012104 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30060, 30140, 30200, 30210, 30310, 30610, 33040, 33100]
   int[] femaleHair = [31070, 31080, 31150, 31300, 31350, 31700, 34050, 34110]
   int[] maleHairExperimental = [30030, 30140, 30200, 30210, 30310, 30610, 33040, 33100]
   int[] femaleHairExperimental = [31070, 31150, 31300, 31350, 31430, 31700, 34050, 34110]
   int[] hairNew = []
   int[] hairColor = []

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
            cm.sendSimple(I18nMessage.from("1012104_HELLO"))
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 3
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (def i = 0; i < maleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (def i = 0; i < femaleHair.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("1012104_REGULAR_EXPLAINED"))
            } else if (selection == 1) {
               beauty = 1
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (def i = 0; i < maleHairExperimental.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHairExperimental[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (def i = 0; i < femaleHairExperimental.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHairExperimental[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }
               cm.sendYesNo(I18nMessage.from("1012104_EXPERIMENTAL_EXPLAINED"))
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (def i = 0; i < 8; i++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, current + i, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendYesNo(I18nMessage.from("1012104_REGULAR_COLOR_EXPLAINED"))
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150010)) {
                  cm.gainItem(5150010, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("1012104_ENJOY_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1012104_NO_STYLE_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151000)) {
                  cm.gainItem(5151000, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk(I18nMessage.from("1012104_ENJOY_HAIR_COLOR"))
               } else {
                  cm.sendOk(I18nMessage.from("1012104_NO_COLOR_COUPON"))
               }
            } else if (beauty == 3) {
               if (cm.haveItem(5150000)) {
                  cm.gainItem(5150000, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("1012104_ENJOY_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1012104_NO_STYLE_COUPON"))
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150010, (short) 1)
                  cm.sendOk(I18nMessage.from("1012104_ENJOY"))
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151000, (short) 1)
                  cm.sendOk(I18nMessage.from("1012104_ENJOY"))
               } else {
                  cm.sendOk(I18nMessage.from("1012104_MORE_MESOS_TO_BUY_COUPON"))
               }
            }
         }
      }
   }
}

NPC1012104 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012104(cm: cm))
   }
   return (NPC1012104) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }