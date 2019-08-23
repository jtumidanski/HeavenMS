package npc


import scripting.npc.NPCConversationManager

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
      if (mode < 1) {  // disposing issue with stylishs found thanks to Vcoc
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple("I'm Brittany the assistant. If you have #b#t5150000##k, #b#t5150010##k or #b#t5151000##k by any chance, then how about letting me change your hairdo?\r\n#L0#Haircut: #i5150000##t5150000##l\r\n#L1#Haircut: #i5150010##t5150010##l\r\n#L2#Dye your hair: #i5151000##t5151000##l")
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 3
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (def i = 0; i < maleHair.length; i++) {
                     pushIfItemExists(hairNew, maleHair[i] + (cm.getPlayer().getHair() % 10))
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (def i = 0; i < femaleHair.length; i++) {
                     pushIfItemExists(hairNew, femaleHair[i] + (cm.getPlayer().getHair() % 10))
                  }
               }
               cm.sendYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that even you didn't think was possible. Are you going to use #b#t5150000##k and really change your hairstyle?")
            } else if (selection == 1) {
               beauty = 1
               hairNew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (def i = 0; i < maleHairExperimental.length; i++) {
                     pushIfItemExists(hairNew, maleHairExperimental[i] + (cm.getPlayer().getHair() % 10))
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (def i = 0; i < femaleHairExperimental.length; i++) {
                     pushIfItemExists(hairNew, femaleHairExperimental[i] + (cm.getPlayer().getHair() % 10))
                  }
               }
               cm.sendYesNo("If you use the EXP coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that even you didn't think was possible. Are you going to use #b#t5150010##k and really change your hairstyle?")
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (def i = 0; i < 8; i++) {
                  pushIfItemExists(hairColor, current + i)
               }
               cm.sendYesNo("If you use a regular coupon your hair will change RANDOMLY. Do you still want to use #b#t5151000##k and change it up?")
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150010)) {
                  cm.gainItem(5150010, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151000)) {
                  cm.gainItem(5151000, (short) -1)
                  cm.setHair(hairColor[Math.floor(Math.random() * hairColor.length).intValue()])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            } else if (beauty == 3) {
               if (cm.haveItem(5150000)) {
                  cm.gainItem(5150000, (short) -1)
                  cm.setHair(hairNew[Math.floor(Math.random() * hairNew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150010, (short) 1)
                  cm.sendOk("Enjoy!")
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151000, (short) 1)
                  cm.sendOk("Enjoy!")
               } else {
                  cm.sendOk("You don't have enough mesos to buy a coupon!")
               }
            }
         }
      }
   }

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
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