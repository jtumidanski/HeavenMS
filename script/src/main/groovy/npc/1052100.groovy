package npc


import scripting.npc.NPCConversationManager

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
   int hairprice = 1000000
   int haircolorprice = 1000000
   int[] mhair_v = [30040, 30130, 30780, 30850, 30860, 30920, 33040]
   int[] fhair_v = [31090, 31140, 31330, 31440, 31760, 31880, 34050]
   int[] hairnew = []
   int[] haircolor

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1)  // disposing issue with stylishs found thanks to Vcoc
      {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple("Hello! I'm Don Giovanni, head of the beauty salon! If you have either #b#t5150003##k or #b#t5151003##k, why don't you let me take care of the rest? Decide what you want to do with your hair...\r\n#L1#Haircut: #i5150003##t5150003##l\r\n#L2#Dye your hair: #i5151003##t5151003##l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
               hairnew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mhair_v.length; i++) {
                     pushIfItemExists(hairnew, mhair_v[i] + (cm.getPlayer().getHair() % 10).toInteger())
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < fhair_v.length; i++) {
                     pushIfItemExists(hairnew, fhair_v[i] + (cm.getPlayer().getHair() % 10).toInteger())
                  }
               }
               cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? If you have #b#t5150003##k I'll change it for you. Choose the one to your liking~.", hairnew)
            } else if (selection == 2) {
               beauty = 2
               haircolor = []
               int current = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  pushIfItemExists(haircolor, current + i)
               }
               cm.sendStyle("I can totally change your haircolor and make it look so good. Why don't you change it up a bit? With #b#t5151003##k I'll change it for you. Choose the one to your liking.", haircolor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5420003)) {
                  cm.setHair(hairnew[selection])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else if (cm.haveItem(5150003)) {
                  cm.gainItem(5150003, (short) -1)
                  cm.setHair(hairnew[selection])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151003)) {
                  cm.gainItem(5151003, (short) -1)
                  cm.setHair(haircolor[selection])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            }
            if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= hairprice) {
                  cm.gainMeso(-hairprice)
                  cm.gainItem(5150003, (short) 1)
                  cm.sendOk("Enjoy!")
               } else if (selection == 1 && cm.getMeso() >= haircolorprice) {
                  cm.gainMeso(-haircolorprice)
                  cm.gainItem(5151003, (short) 1)
                  cm.sendOk("Enjoy!")
               } else {
                  cm.sendOk("You don't have enough mesos to buy a coupon!")
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