package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012009 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] mface_r = [20003, 20011, 20021, 20022, 20023, 20027, 20031]
   int[] fface_r = [21004, 21007, 21010, 21012, 21020, 21021, 21030]
   int[] facenew = []

   def start() {
      cm.sendSimple("Hi, I pretty much shouldn't be doing this, but with a #b#t5152004##k, I will do it anyways for you. But don't forget, it will be random!\r\n#L2#Plastic Surgery: #i5152004##t5152004##l")
   }

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 2) {
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
               cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152004##k?")
            }
         } else if (status == 2) {
            if (cm.haveItem(5152004)) {
               cm.gainItem(5152004, (short) -1)
               cm.setFace(facenew[Math.floor(Math.random() * facenew.length).intValue()])
               cm.sendOk("Enjoy your new and improved face!")
            } else {
               cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
            }

            cm.dispose()
         }
      }
   }
}

NPC2012009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012009(cm: cm))
   }
   return (NPC2012009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }