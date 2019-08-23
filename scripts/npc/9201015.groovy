package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201015 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairprice = 1000000
   int haircolorprice = 1000000
   int[] mhair_v = [30050, 30300, 30410, 30450, 30510, 30570, 30580, 30590, 30660, 30910]
   int[] fhair_v = [31150, 31220, 31260, 31310, 31420, 31480, 31490, 31580, 31590, 31610, 31630]
   int[] hairnew = []
   int[] haircolor = []

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
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
            cm.sendSimple("Welcome to the Amoria hair shop. If you have a #b#t5150020##k, or a #b#t5151017##k, allow me to take care of your hairdo. Please choose the one you want.\r\n#L1#Haircut: #i5150020##t5150020##l\r\n#L2#Dye your hair: #i5151017##t5151017##l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_v.length; i++) {
                     pushIfItemExists(hairnew, mhair_v[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < fhair_v.length; i++) {
                     pushIfItemExists(hairnew, fhair_v[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? With #b#t5150020##k, I'll take care of the rest for you. Choose the style of your liking!", hairnew)
            } else if (selection == 2) {
               beauty = 2
               haircolor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  pushIfItemExists(haircolor, current + i)
               }
               cm.sendStyle("I can totally change your haircolor and make it look so good. Why don't you change it up a bit? With #b#t5151017##k, I'll take care of the rest. Choose the color of your liking!", haircolor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150020)) {
                  cm.gainItem(5150020, (short) -1)
                  cm.setHair(hairnew[selection])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151017)) {
                  cm.gainItem(5151017, (short) -1)
                  cm.setHair(haircolor[selection])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairprice) {
                  cm.gainMeso(-hairprice)
                  cm.gainItem(5150020, (short) 1)
                  cm.sendOk("Enjoy!")
               } else if (selection == 1 && cm.getMeso() >= haircolorprice) {
                  cm.gainMeso(-haircolorprice)
                  cm.gainItem(5151017, (short) 1)
                  cm.sendOk("Enjoy!")
               } else {
                  cm.sendOk("You don't have enough mesos to buy a coupon!")
               }
            }
         }
      }
   }
}

NPC9201015 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201015(cm: cm))
   }
   return (NPC9201015) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }