package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201061 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int price = 1000000
   int[] colors = []

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
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple("Hi, there~! I'm Bomack. If you have a #b#t5152035##k, I can prescribe the right kind of cosmetic lenses for you. Now, what would you like to do?\r\n#L2#Cosmetic Lens: #i5152035##t5152035##l")
         } else if (status == 1) {
            if (selection == 2) {
               int current = 0
               if (cm.getPlayer().getGender() == 0) {
                  current = cm.getPlayer().getFace() % 100 + 20000
               } else if (cm.getPlayer().getGender() == 1) {
                  current = cm.getPlayer().getFace() % 100 + 21000
               }
               colors = []
               int[] temp = [current + 100, current + 200, current + 300, current + 400, current + 500, current + 600, current + 700]
               pushIfItemsExists(colors, temp)
               cm.sendYesNo("If you use the regular coupon, you'll be awarded a random pair of cosmetic lenses. Are you going to use #b#t5152035##k and really make the change to your eyes?")
            }
         } else if (status == 2) {
            cm.dispose()
            if (cm.haveItem(5152035)) {
               cm.gainItem(5152035, (short) -1)
               cm.setFace(colors[Math.floor(Math.random() * colors.length).intValue()])
               cm.sendOk("Enjoy your new and improved cosmetic lenses!")
            } else {
               cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..")
            }
         }
      }
   }
}

NPC9201061 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201061(cm: cm))
   }
   return (NPC9201061) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }