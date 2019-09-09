package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] mface_r = [20001, 20003, 20009, 20010, 20025, 20031]
   int[] fface_r = [21002, 21009, 21011, 21013, 21016, 21029, 21030]
   int[] facenew = []
   int[] colors = []

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def pushIfItemsExists(int[] array, int[] itemidList) {
      for (int i = 0; i < itemidList.length; i++) {
         int itemid = itemidList[i]

         if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
            array << itemid
         }
      }
   }

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {  // disposing issue with stylishs found thanks to Vcoc
         if (type == 7) {
            cm.sendNext("I see...take your time, see if you really want it. Let me know when you make up your mind.")
         }

         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple("Hi, I'm the face surgery assistant doctor from around here. With a #b#t5152029##k or a #b#t5152048##k, I can make it kick in just nice, trust me. Ah, don't forget, what comes next after the operation will be random! Then, what are you going for?\r\n#L1#Plastic Surgery: #i5152029##t5152029##l\r\n#L2#Cosmetic Lens: #i5152048##t5152048##l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 0

               facenew = []
               if (cm.getChar().getGender() == 0) {
                  for (int i = 0; i < mface_r.length; i++) {
                     pushIfItemExists(facenew, mface_r[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100))
                  }
               }
               if (cm.getChar().getGender() == 1) {
                  for (int i = 0; i < fface_r.length; i++) {
                     pushIfItemExists(facenew, fface_r[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100))
                  }
               }
               cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152029##k?")
            } else if (selection == 2) {
               beauty = 1
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               colors = []
               int[] temp = [current, current + 100, current + 300, current + 600, current + 700]
               pushIfItemsExists(colors, temp)
               cm.sendYesNo("If you use the regular coupon, you'll be awarded a random pair of cosmetic lenses. Are you going to use a #b#t5152048##k and really make the change to your eyes?")
            }
         } else if (status == 2) {
            cm.dispose()

            if (beauty == 0) {
               if (cm.haveItem(5152029)) {
                  cm.gainItem(5152029, (short) -1)
                  cm.setFace(facenew[Math.floor(Math.random() * facenew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved face!")
               } else {
                  cm.sendNext("Um ... it looks like you don't have the coupon specifically for this place...sorry to say this, but without the coupon, there's no plastic surgery for you.")
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152048)) {
                  cm.gainItem(5152048, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
               } else {
                  cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               }
            }
         }
      }
   }
}

NPC2100009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100009(cm: cm))
   }
   return (NPC2100009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }