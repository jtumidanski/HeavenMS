package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100008 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] mface_v = [20000, 20004, 20005, 20012, 20013, 20031]
   int[] fface_v = [21000, 21003, 21006, 21009, 21012, 21024]
   int[] facenew = []
   int[] colors = []
   int current

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
            cm.sendSimple("Ah, welcome to the Ariant Plastic Surgery! Would you like to transform your face into something new? With a #b#t5152030##k or a #b#t5152047##k, I can make your face so much better!\r\n#L1#Plastic Surgery: #i5152030##t5152030##l\r\n#L2#Cosmetic Lens: #i5152047##t5152047##l\r\n#L3#One-time Cosmetic Lenses: #i5152101# (any color)#l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 0

               facenew = []
               if (cm.getChar().getGender() == 0) {
                  for (int i = 0; i < mface_v.length; i++) {
                     pushIfItemExists(facenew, mface_v[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100))
                  }
               }
               if (cm.getChar().getGender() == 1) {
                  for (int i = 0; i < fface_v.length; i++) {
                     pushIfItemExists(facenew, fface_v[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100))
                  }
               }
               cm.sendStyle("Hmmm... Face of beauty glows even under cover and burning desert. Choose the face you want, and I will pull out my outstanding skill for the great make over.", facenew)
            } else if (selection == 2) {
               beauty = 1

               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               colors = []
               int[] temp = [current, current + 100, current + 300, current + 600, current + 700]
               pushIfItemsExists(colors, temp)
               cm.sendStyle("With the utmost finesse matching that of the sparkling sands of the desert that gleefully embraces the rooftop of the Palace, we will make your eyes shine even brighter with the new lenses. Select the one you want to use...", colors)
            } else if (selection == 3) {
               beauty = 3
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
               if (cm.haveItem(5152030)) {
                  cm.gainItem(5152030, (short) -1)
                  cm.setFace(facenew[selection])
                  cm.sendOk("Enjoy your new and improved face!")
               } else {
                  cm.sendNext("Erm... You don't seem to have the exclusive coupon for this hospital. Without the coupon, I'm afraid I can't do it for you.")
               }
            } else if (beauty == 1) {
               if (cm.haveItem(5152047)) {
                  cm.gainItem(5152047, (short) -1)
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

NPC2100008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100008(cm: cm))
   }
   return (NPC2100008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }