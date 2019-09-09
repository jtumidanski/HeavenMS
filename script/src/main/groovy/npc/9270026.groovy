package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270026 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int current
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
      cm.sendSimple("Hi, there! I'm Sixx, in charge of Da Yan Jing Lens Shop here at CBD! With #b#t5152039##k or #b#t5152040##k, you can let us take care of the rest and have the kind of beautiful look you've always craved! Remember, the first thing everyone notices about you are the eyes, and we can help you find the cosmetic lens that most fits you! Now, what would you like to use?\r\n#L1#Cosmetic Lenses: #i5152039##t5152039##l\r\n#L2#Cosmetic Lenses: #i5152040##t5152040##l\r\n#L3#One-time Cosmetic Lenses: #i5152107# (any color)#l")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 1) {
               beauty = 1
               current = cm.getPlayer().getFace() % 100 + 20000 + cm.getPlayer().getGender() * 1000
               cm.sendYesNo("If you use the regular coupon, you'll be awarded a random pair of cosmetic lenses. Are you going to use a #b#t5152039##k and really make the change to your eyes?")
            } else if (selection == 2) {
               beauty = 2
               current = cm.getPlayer().getFace() % 100 + 20000 + cm.getPlayer().getGender() * 1000
               int[] temp = [current + 200, current + 300, current + 400, current + 700]
               pushIfItemsExists(colors, temp)
               cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of lens would you like to wear? Choose the style of your liking.", colors)
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
            if (beauty == 1) {
               if (cm.haveItem(5152039)) {
                  cm.gainItem(5152039, (short) -1)
                  cm.setFace(Math.floor(Math.random() * 8).intValue() * 100 + current)
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
               } else {
                  cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..")
               }
            } else if (beauty == 2) {
               if (cm.haveItem(5152040)) {
                  cm.gainItem(5152040, (short) -1)
                  cm.setFace(colors[selection])
                  cm.sendOk("Enjoy your new and improved cosmetic lenses!")
               } else {
                  cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..")
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
            cm.dispose()
         }
      }
   }
}

NPC9270026 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270026(cm: cm))
   }
   return (NPC9270026) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }