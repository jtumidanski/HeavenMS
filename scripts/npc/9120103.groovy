package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120103 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] mface_r = [20000, 20016, 20019, 20020, 20021, 20024, 20026]
   int[] fface_r = [21000, 21002, 21009, 21016, 21022, 21025, 21027]
   int[] facenew = []
   int[] colors = []

   def start() {
      cm.sendSimple("Hi, I pretty much shouldn't be doing this, but with a #b#t5152008##k or a #b#t5152046##k, I will do it anyways for you. But don't forget, it will be random!\r\n#L1#Plastic Surgery: #i5152008##t5152008##l\r\n#L2#Cosmetic Lens: #i5152046##t5152046##l")
   }

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

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 1) {
               beauty = 0
               facenew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mface_r.length; i++) {
                     pushIfItemExists(facenew, mface_r[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
                  }
               } else {
                  for (int i = 0; i < fface_r.length; i++) {
                     pushIfItemExists(facenew, fface_r[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
                  }
               }
               cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152008##k?")
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
               int[] temp = [current, current + 100, current + 200, current + 300, current + 400, current + 500, current + 700]
               pushIfItemsExists(colors, temp)
               cm.sendYesNo("If you use the regular coupon, you'll be awarded a random pair of cosmetic lenses. Are you going to use a #b#t5152046##k and really make the change to your eyes?")
            }
         } else if (status == 2) {
            if (beauty == 0) {
               if (cm.haveItem(5152008)) {
                  cm.gainItem(5152008, (short) -1)
                  cm.setFace(facenew[Math.floor(Math.random() * facenew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved face!")
               } else {
                  cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152046)) {
                  cm.gainItem(5152046, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
               } else {
                  cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC9120103 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120103(cm: cm))
   }
   return (NPC9120103) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }