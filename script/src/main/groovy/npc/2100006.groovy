package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100006 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] mhair_v = [30150, 30170, 30180, 30320, 30330, 30410, 30460, 30820, 30900]
   int[] fhair_v = [31040, 31090, 31190, 31330, 31340, 31400, 31420, 31620, 31660]
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
            cm.sendSimple("Hahaha... it takes a lot of style and flair for someone to pay attention to his or her hairsyle in a desert. Someone like you...If you have #bAriant hair style coupon(VIP)#k or #bAriant hair color coupon(VIP)#k, I'll give your hair a fresh new look. \r\n#L0#Haircut: #i5150027##t5150027##l\r\n#L1#Dye your hair: #i5151022##t5151022##l")
         } else if (status == 1) {
            if (selection == 0) {
               beauty = 1
               hairnew = []
               if (cm.getChar().getGender() == 0) {
                  for (int i = 0; i < mhair_v.length; i++) {
                     pushIfItemExists(hairnew, mhair_v[i] + (cm.getChar().getHair() % 10).intValue())
                  }
               }
               if (cm.getChar().getGender() == 1) {
                  for (int i = 0; i < fhair_v.length; i++) {
                     pushIfItemExists(hairnew, fhair_v[i] + (cm.getChar().getHair() % 10).intValue())
                  }
               }
               cm.sendStyle("Hahaha~all you need is #bAriant hair style coupon(VIP)#k to change up your hairstyle. Choose the new style, and let me do the rest.", hairnew)
            } else if (selection == 1) {
               beauty = 2
               haircolor = []
               int current = (cm.getChar().getHair() / 10).intValue() * 10
               for (int i = 0; i < 8; i++) {
                  pushIfItemExists(haircolor, current + i)
               }
               cm.sendStyle("Every once in a while, it doesn't hurt to change up your hair color... it's fun. Allow me, the great Mazra, to dye your hair, so you just bring me #bAriant hair color coupon(VIP)#k, and choose your new hair color.", haircolor)
            }
         } else if (status == 2) {
            cm.dispose()
            if (beauty == 1) {
               if (cm.haveItem(5150027)) {
                  cm.gainItem(5150027, (short) -1)
                  cm.setHair(hairnew[selection])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendNext("I thought I told you, you need the coupon in order for me to work magic on your hair check again.")
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151022)) {
                  cm.gainItem(5151022, (short) -1)
                  cm.setHair(haircolor[selection])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendNext("I thought I told you, you need the coupon in order for me to work magic on your hair check again.")
               }
            }
         }
      }
   }
}

NPC2100006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100006(cm: cm))
   }
   return (NPC2100006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }