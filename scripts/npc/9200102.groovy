package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9200102 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int regprice = 1000000
   int vipprice = 1000000
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
            cm.sendSimple("Um... hi, I'm Dr. Bosch, and I am a cosmetic lens expert here at the Ludibrium Plastic Surgery Shop. I believe your eyes are the most important feature in your body, and with #b#t5152012##k or #b#t5152015##k, I can prescribe the right kind of cosmetic lenses for you. Now, what would you like to use?\r\n#L1#Cosmetic Lenses: #i5152012##t5152012##l\r\n#L2#Cosmetic Lenses: #i5152015##t5152015##l\r\n#L3#One-time Cosmetic Lenses: #i5152105# (any color)#l")
         } else if (status == 1) {
            if (selection == 1) {
               beauty = 1
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               colors = []
               int[] temp = [current + 200, current + 300, current + 400, current + 500, current + 700]
               pushIfItemsExists(colors, temp)
               cm.sendYesNo("If you use the regular coupon, you'll be awarded a random pair of cosmetic lenses. Are you going to use a #b#t5152012##k and really make the change to your eyes?")
            } else if (selection == 2) {
               beauty = 2
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               }
               if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               colors = []
               int[] temp = [current + 200, current + 300, current + 400, current + 500, current + 700]
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
            if (beauty == 1) {
               if (cm.haveItem(5152012)) {
                  cm.gainItem(5152012, (short) -1)
                  cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
                  cm.dispose()
               } else {
                  cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..")
                  cm.dispose()
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5152015)) {
                  cm.gainItem(5152015, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
                  cm.dispose()
               } else {
                  cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..")
                  cm.dispose()
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
            } else if (beauty == 0) {
               if (selection == 0 && cm.getMeso() >= regprice) {
                  cm.gainMeso(-regprice)
                  cm.gainItem(5152012, (short) 1)
                  cm.sendOk("Enjoy!")
                  cm.dispose()
               } else if (selection == 1 && cm.getMeso() >= vipprice) {
                  cm.gainMeso(-vipprice)
                  cm.gainItem(5152015, (short) 1)
                  cm.sendOk("Enjoy!")
                  cm.dispose()
               } else {
                  cm.sendOk("You don't have enough mesos to buy a coupon!")
               }
            }
         }
      }
   }
}

NPC9200102 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9200102(cm: cm))
   }
   return (NPC9200102) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }