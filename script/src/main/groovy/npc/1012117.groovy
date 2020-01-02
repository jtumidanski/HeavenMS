package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

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
            cm.sendSimple("Hi, I'm #p1012117#, the most charming and stylish stylist around. If you're looking for the best looking hairdos around, look no further!\r\n#L0##i5150040##t5150040##l\r\n#L1##i5150044##t5150044##l")
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 1
               cm.sendYesNo("If you use this REGULAR coupon, your hair may transform into a random new look...do you still want to do it using #b#t5150040##k, I will do it anyways for you. But don't forget, it will be random!")
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

               cm.sendStyle("Using the SPECIAL coupon you can choose the style your hair will become. Pick the style that best provides you delight...", hairNew)
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
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5150044)) {
                  cm.gainItem(5150044, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
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