package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1012117 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] maleHair = [30010, 30070, 30080, 30090, 30100, 30690, 30760, 33000]
   int[] femaleHair = [31130, 31530, 31820, 31920, 31940, 34000, 34030]
   int[] maleHairVip = [30010, 30070, 30080, 30090, 30100, 30480, 30560, 30690, 30760, 30850, 30890, 30930, 30950]
   int[] femaleHairVip = [31020, 31130, 31510, 31530, 31820, 31860, 31890, 31920, 31940, 31950, 34000]
   int[] hairNew = []
   int beauty

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
            cm.sendSimple(I18nMessage.from("1012117_HELLO"))
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 1
               cm.sendYesNo(I18nMessage.from("1012117_REGULAR_EXPLAINED"))
            } else {
               beauty = 2

               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (def i = 0; i < maleHairVip.length; i++) {
                     hairNew  = ScriptUtils.pushItemIfTrue(hairNew, maleHairVip[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               } else {
                  for (def i = 0; i < femaleHairVip.length; i++) {
                     hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHairVip[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                  }
               }

               cm.sendStyle(I18nMessage.from("1012117_SPECIAL_EXPLAINED"), hairNew)
            }
         } else if (status == 2) {
            if (beauty == 1) {
               if (cm.haveItem(5150040)) {
                  hairNew = []
                  if (cm.getPlayer().getGender() == 0) {
                     for (def i = 0; i < maleHair.length; i++) {
                        hairNew = ScriptUtils.pushItemIfTrue(hairNew, maleHair[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                     }
                  } else {
                     for (def i = 0; i < femaleHair.length; i++) {
                        hairNew = ScriptUtils.pushItemIfTrue(hairNew, femaleHair[i] + (cm.getPlayer().getHair() % 10), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
                     }
                  }

                  cm.gainItem(5150040, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk(I18nMessage.from("1012117_ENJOY_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1012117_NO_STYLE_COUPON"))
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5150044)) {
                  cm.gainItem(5150044, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk(I18nMessage.from("1012117_ENJOY_HAIR_STYLE"))
               } else {
                  cm.sendOk(I18nMessage.from("1012117_NO_STYLE_COUPON"))
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC1012117 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012117(cm: cm))
   }
   return (NPC1012117) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }