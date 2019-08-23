package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201070 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] mface_r = [20001, 20008, 20011, 20013, 20024, 20029, 20032]
   int[] fface_r = [21000, 21007, 21011, 21012, 21017, 21020, 21022]
   int[] facenew = []

   def start() {
      cm.sendSimple("Hi, I pretty much shouldn't be doing this, but with a #b#t5152033##k, I will do it anyways for you. But don't forget, it will be random!\r\n#L2#Plastic Surgery: #i5152033##t5152033##l")
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
         if (status == 1) {
            if (selection == 2) {
               facenew = []
               if (cm.getPlayer().getGender() == 0)
                  for (int i = 0; i < mface_r.length; i++ )
               pushIfItemExists(facenew, mface_r[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
               if (cm.getPlayer().getGender() == 1)
                  for (int i = 0; i < fface_r.length; i++ )
               pushIfItemExists(facenew, fface_r[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
               cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152033##k?")
            }
         } else if (status == 2) {
            if (cm.haveItem(5152033)) {
               cm.gainItem(5152033, (short) -1)
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

NPC9201070 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201070(cm: cm))
   }
   return (NPC9201070) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }