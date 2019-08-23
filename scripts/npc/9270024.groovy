package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270024 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] mface_v = [20005, 20012, 20013, 20020, 20021, 20026]
   int[] fface_v = [21006, 21009, 21011, 21012, 21021, 21025]
   int[] facenew = []

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
      if (mode < 1)  // disposing issue with stylishs found thanks to Vcoc
      {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple("Let's see...I can totally transform your face into something new. Don't you want to try it? For #b#t5152038##k, you can get the face of your liking. Take your time in choosing the face of your preference...\r\n#L2#Let me get my dream face! (Uses #i5152038# #t5152038#)#l")
         } else if (status == 1) {
            if (!cm.haveItem(5152038)) {
               cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               cm.dispose()
               return
            }

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
            cm.sendStyle("Let's see... I can totally transform your face into something new. Don't you want to try it? For #b#t5152038##k, you can get the face of your liking. Take your time in choosing the face of your preference...", facenew)
         } else if (status == 2) {
            cm.gainItem(5152038, (short) -1)
            cm.setFace(facenew[selection])
            cm.sendOk("Enjoy your new and improved face!")

            cm.dispose()
         }
      }
   }
}

NPC9270024 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270024(cm: cm))
   }
   return (NPC9270024) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }