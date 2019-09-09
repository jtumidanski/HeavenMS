package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270025 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] skin = [0, 1, 2, 3, 4]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
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
            cm.sendSimple("Well, hello! Welcome to the Lian Hua Hua Skin-Care! Would you like to have a firm, tight, healthy looking skin like mine?  With #b#tCBD Skin Coupon##k, you can let us take care of the rest and have the kind of skin you've always wanted!\r\n#L1#Sounds Good! (uses #i5153010# #t5153010#)#l")
         } else if (status == 1) {
            if (!cm.haveItem(5153010)) {
               cm.sendOk("It looks like you don't have the coupon you need to receive the treatment. I'm sorry but it looks like we cannot do it for you.")
               cm.dispose()
               return
            }
            cm.sendStyle("With our specialized service, you can see the way you'll look after the treatment in advance. What kind of a skin-treatment would you like to do? Go ahead and choose the style of your liking...", skin)
         } else {
            cm.gainItem(5153010, (short) -1)
            cm.setSkin(selection)
            cm.sendOk("Enjoy your new and improved skin!")

            cm.dispose()
         }
      }
   }
}

NPC9270025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270025(cm: cm))
   }
   return (NPC9270025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }