package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120102 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] mface_v = [20000, 20004, 20005, 20012, 20020, 20031]
   int[] fface_v = [21000, 21003, 21006, 21012, 21021, 21024]
   int[] facenew = []
   int[] colors = []

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
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
      if (mode < 1) {  // disposing issue with stylishs found thanks to Vcoc
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple("Well well well, welcome to the Showa Plastic Surgery! Would you like to transform your face into something new? With a #b#t5152009##k or a #b#t5152045##k, you can let us take care of the rest and have the face you've always wanted~!\r\n#L1#Plastic Surgery: #i5152009##t5152009##l\r\n#L2#Cosmetic Lens: #i5152045##t5152045##l\r\n#L3#One-time Cosmetic Lenses: #i5152102# (any color)#l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 0
               facenew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mface_v.length; i++) {
                     pushIfItemExists(facenew, mface_v[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < fface_v.length; i++) {
                     pushIfItemExists(facenew, fface_v[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
                  }
               }
               cm.sendStyle("I can totally transform your face into something new... how about giving us a try? For #b#t5152009##k, you can get the face of your liking...take your time in choosing the face of your preference.", facenew)
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
               cm.sendStyle("With our new computer program, you can see yourself after the treatment in advance. What kind of lens would you like to wear? Please choose the style of your liking.", colors)
            } else if (selection == 3) {
               beauty = 3
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }

               colors = []
               for (int i = 0; i < 8; i++) {
                  if (cm.haveItem(5152100 + i)) {
                     pushIfItemExists(colors, current + 100 * i)
                  }
               }

               if (colors.length == 0) {
                  cm.sendOk("You don't have any One-Time Cosmetic Lens to use.")
                  cm.dispose()
                  return
               }

               cm.sendStyle("What kind of lens would you like to wear? Please choose the style of your liking.", colors)
            }
         } else if (status == 2) {
            cm.dispose()

            if (beauty == 0) {
               if (cm.haveItem(5152009)) {
                  cm.gainItem(5152009, (short) -1)
                  cm.setFace(facenew[selection])
                  cm.sendOk("Enjoy your new and improved face!")
               } else {
                  cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152045)) {
                  cm.gainItem(5152045, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
               } else {
                  cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               }
            } else if (beauty == 3) {
               int color = (colors[selection] / 100) % 100 | 0

               if (cm.haveItem(5152100 + color)) {
                  cm.gainItem(5152100 + color, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
               } else {
                  cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..")
               }
            }
         }
      }
   }
}

NPC9120102 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120102(cm: cm))
   }
   return (NPC9120102) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }