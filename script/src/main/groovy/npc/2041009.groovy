package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int beauty = 0
   int hairprice = 1000000
   int haircolorprice = 1000000
   int[] mhair_r = [30190, 30220, 30250, 30540, 30610, 30620, 30640, 30650, 30660, 30840, 30870, 30940, 30990]
   int[] fhair_r = [31170, 31270, 31290, 31510, 31540, 31550, 31600, 31640, 31680, 31810, 31830, 31840, 31870]
   int[] mhair_e = [30030, 30190, 30220, 30250, 30540, 30610, 30620, 30640, 30650, 30660, 30840, 30990]
   int[] fhair_e = [31170, 31270, 31430, 31510, 31540, 31550, 31600, 31680, 31810, 31830, 31840, 31870]
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
            cm.sendSimple("Hi, I'm the assistant here. Don't worry, I'm plenty good enough for this. If you have #b#t5150006##k, #b#t5150012##k or #b#t5151006##k by any chance, then allow me to take care of the rest, alright?\r\n#L0#Haircut: #i5150006##t5150006##l\r\n#L1#Haircut: #i5150012##t5150012##l\r\n#L2#Dye your hair: #i5151006##t5151006##l")
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 3
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_r.length; i++) {
                     pushIfItemExists(hairnew, mhair_r[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < fhair_r.length; i++) {
                     pushIfItemExists(hairnew, fhair_r[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               cm.sendYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150012##k and really change your hairstyle?")
            } else if (selection == 1) {
               beauty = 1
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_e.length; i++) {
                     pushIfItemExists(hairnew, mhair_e[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < fhair_e.length; i++) {
                     pushIfItemExists(hairnew, fhair_e[i] + (cm.getPlayer().getHair() % 10).intValue())
                  }
               }
               cm.sendYesNo("If you use the EXP coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150012##k and really change your hairstyle?")
            } else if (selection == 2) {
               beauty = 2
               haircolor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  pushIfItemExists(haircolor, current + i)
               }
               cm.sendYesNo("If you use a regular coupon your hair will change RANDOMLY. Do you still want to use #b#t5151006##k and change it up?")
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150012)) {
                  cm.gainItem(5150012, (short) -1)
                  cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5151006)) {
                  cm.gainItem(5151006, (short) -1)
                  cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length).intValue()])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            } else if (beauty == 3) {
               if (cm.haveItem(5150006)) {
                  cm.gainItem(5150006, (short) -1)
                  cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length).intValue()])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairprice) {
                  cm.gainMeso(-hairprice)
                  cm.gainItem(5150012, (short) 1)
                  cm.sendOk("Enjoy!")
               } else if (selection == 1 && cm.getMeso() >= haircolorprice) {
                  cm.gainMeso(-haircolorprice)
                  cm.gainItem(5151006, (short) 1)
                  cm.sendOk("Enjoy!")
               } else {
                  cm.sendOk("You don't have enough mesos to buy a coupon!")
               }
            }
         }
      }
   }
}

NPC2041009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041009(cm: cm))
   }
   return (NPC2041009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }