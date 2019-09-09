package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270023 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] mface_r = [20002, 20005, 20006, 20013, 20017, 20021, 20024]
   int[] fface_r = [21002, 21003, 21014, 21016, 21017, 21021, 21027]
   int[] facenew = []

   def start() {
      cm.sendSimple("If you use this regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152037##k, I will do it anyways for you. But don't forget, it will be random!\r\n#L2#OK! (Uses #i5152037# #t5152037#)#l")
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

         if (status == 1) {
            if (!cm.haveItem(5152037)) {
               cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               cm.dispose()
               return
            }

            facenew = []
            if (cm.getPlayer().getGender() == 0) {
               for (int i = 0; i < mface_r.length; i++) {
                  pushIfItemExists(facenew, mface_r[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
               }
            }
            if (cm.getPlayer().getGender() == 1) {
               for (int i = 0; i < fface_r.length; i++) {
                  pushIfItemExists(facenew, fface_r[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
               }
            }
            cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152037##k?")
         } else if (status == 2) {
            cm.gainItem(5152037, (short) -1)
            cm.setFace(facenew[Math.floor(Math.random() * facenew.length).intValue()])
            cm.sendOk("Enjoy your new and improved face!")

            cm.dispose()
         }
      }
   }
}

NPC9270023 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270023(cm: cm))
   }
   return (NPC9270023) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }