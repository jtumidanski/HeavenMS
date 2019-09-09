package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Natalie
	Map(s): 		Henesys VIP Hair/Hair Color Change
	Description: 	
*/


class NPC1012103 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairPrice = 1000000
   int hairColorPrice = 1000000
   int[] maleHair = [30060, 30140, 30200, 30210, 30310, 33040, 33100]
   int[] femaleHair = [31150, 31300, 31350, 31700, 31740, 34050, 34110]
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
         status++
         if (status == 0) {
            cm.sendSimple("I'm the head of this hair salon. If you have a #b#t5150001##k or a #b#t5151001##k allow me to take care of your hairdo. Please choose the one you want.\r\n#L1#Haircut: #i5150001##t5150001##l\r\n#L2#Dye your hair: #i5151001##t5151001##l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
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
               cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? If you have #b#t5150001##k I'll change it for you. Choose the one to your liking~.", hairNew)
            } else if (selection == 2) {
               beauty = 2
               hairColor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (def i = 0; i < 8; i++) {
                  pushIfItemExists(hairColor, current + i)
               }
               cm.sendStyle("I can totally change your haircolor and make it look so good. Why don't you change it up a bit? With #b#t51051001##k I'll change it for you. Choose the one to your liking.", hairColor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150001)) {
                  cm.gainItem(5150001, (short) -1)
                  cm.setHair(hairNew[selection])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151001)) {
                  cm.gainItem(5151001, (short) -1)
                  cm.setHair(hairColor[selection])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairPrice) {
                  cm.gainMeso(-hairPrice)
                  cm.gainItem(5150001, (short) 1)
                  cm.sendOk("Enjoy!")
               } else if (selection == 1 && cm.getMeso() >= hairColorPrice) {
                  cm.gainMeso(-hairColorPrice)
                  cm.gainItem(5151001, (short) 1)
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

NPC1012103 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012103(cm: cm))
   }
   return (NPC1012103) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }