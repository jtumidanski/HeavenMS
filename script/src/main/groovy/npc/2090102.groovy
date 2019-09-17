package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2090102 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int price = 1000000
   int[] skin = [0, 1, 2, 3, 4]

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
            cm.sendSimple("Well, hello! Welcome to the Mu Lung Skin-Care! Would you like to have a firm, tight, healthy looking skin like mine?  With #b#t5153006##k, you can let us take care of the rest and have the kind of skin you've always wanted~!\r\n#L2#Skin Care: #i5153006##t5153006##l")
         } else if (status == 1) {
            if (selection == 2) {
               cm.sendStyle("With our specialized machine, you can see the way you'll look after the treatment PRIOR to the procedure. What kind of a look are you looking for? Go ahead and choose the style of your liking~!", skin)
            }
         } else if (status == 2) {
            cm.dispose()
            if (cm.haveItem(5153006)) {
               cm.gainItem(5153006, (short) -1)
               cm.setSkin(skin[selection])
               cm.sendOk("Enjoy your new and improved skin!")
            } else {
               cm.sendOk("Um...you don't have the skin-care coupon you need to receive the treatment. Sorry, but I am afraid we can't do it for you...")
            }
         }
      }
   }
}

NPC2090102 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2090102(cm: cm))
   }
   return (NPC2090102) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }