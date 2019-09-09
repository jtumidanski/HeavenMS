package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012007 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int hairprice = 1000000
   int haircolorprice = 1000000
   int[] mhair_d = [30030, 30020, 30000, 30270, 30230]
   int[] fhair_d = [31040, 31000, 31250, 31220, 31260]
   int[] mhair_r = [30230, 30260, 30280, 30340, 30490, 30530, 30630, 30740]
   int[] fhair_r = [31110, 31220, 31230, 31630, 31650, 31710, 31790, 31890, 31930]
   int[] mhair_e = [30230, 30280, 30340, 30490, 30530, 30740]
   int[] fhair_e = [31110, 31220, 31230, 31710, 31790, 31890, 31930]
   int[] hairnew = []
   int[] haircolor = []

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def start() {
      cm.sendSimple("I'm Rinz, the assistant. Do you have #b#t5154000##k, #b#t5150004##k, #b#t5150013##k or #b#t5151004##k with you? If so, what do you think about letting me take care of your hairdo? What do you want to do with your hair?\r\n#L0#Haircut: #i5154000##t5154000##l\r\n#L1#Haircut: #i5150004##t5150004##l\r\n#L2#Haircut: #i5150013##t5150013##l\r\n#L3#Dye your hair: #i5151004##t5151004##l")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               beauty = 4
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_d.length; i++) {
                     pushIfItemExists(hairnew, mhair_d[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               } else {
                  for (int i = 0; i < fhair_d.length; i++) {
                     pushIfItemExists(hairnew, fhair_d[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               cm.sendYesNo("If you use the DRT coupon your hair will change RANDOMLY with a chance to obtain the basic styles that I came up with. Are you going to use #b#t5154000##k and really change your hairstyle?")
            } else if (selection == 1) {
               beauty = 3
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_r.length; i++) {
                     pushIfItemExists(hairnew, mhair_r[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               } else {
                  for (int i = 0; i < fhair_r.length; i++) {
                     pushIfItemExists(hairnew, fhair_r[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               cm.sendYesNo("If you use the REG coupon your hair will change RANDOMLY. Are you going to use #b#t5150004##k and really change your hairstyle?")
            } else if (selection == 2) {
               beauty = 1
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_e.length; i++) {
                     pushIfItemExists(hairnew, mhair_e[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               } else {
                  for (int i = 0; i < fhair_e.length; i++) {
                     pushIfItemExists(hairnew, fhair_e[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               cm.sendYesNo("If you use the EXP coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150013##k and really change your hairstyle?")
            } else if (selection == 3) {
               beauty = 2
               haircolor = []
               int current = (cm.getPlayer().getHair() / 10) | 0
               for (int i = 0; i < 8; i++) {
                  pushIfItemExists(haircolor, current + i)
               }
               cm.sendYesNo("If you use a regular coupon your hair color will change RANDOMLY. Do you still want to use #b#t5151004##k and change it up?")
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150013)) {
                  cm.gainItem(5150013, (short) -1)
                  cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151004)) {
                  cm.gainItem(5151004, (short) -1)
                  cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length).intValue()])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            } else if (beauty == 3) {
               if (cm.haveItem(5150004)) {
                  cm.gainItem(5150004, (short) -1)
                  cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 4) {
               if (cm.haveItem(5154000)) {
                  cm.gainItem(5154000, (short) -1)
                  cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairprice) {
                  cm.gainMeso(-hairprice)
                  cm.gainItem(5150013, (short) 1)
                  cm.sendOk("Enjoy!")
               } else if (selection == 1 && cm.getMeso() >= haircolorprice) {
                  cm.gainMeso(-haircolorprice)
                  cm.gainItem(5151004, (short) 1)
                  cm.sendOk("Enjoy!")
               } else {
                  cm.sendOk("You don't have enough mesos to buy a coupon!")
               }
            }
         }
      }
   }
}

NPC2012007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012007(cm: cm))
   }
   return (NPC2012007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }