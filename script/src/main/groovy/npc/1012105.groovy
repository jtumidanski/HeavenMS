package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Ms. Tan
	Map(s): 		Henesys Skin Change
	Description: 	
*/


class NPC1012105 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int[] skin = [0, 1, 2, 3, 4]
   int price = 1000000

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple("Well, hello! Welcome to the Henesys Skin-Care! Would you like to have a firm, tight, healthy looking skin like mine?  With a #b#t5153000##k, you can let us take care of the rest and have the kind of skin you've always wanted~!\r\n#L1#Skin Care: #i5153000##t5153000##l")
         } else if (status == 1) {
            if (cm.haveItem(5153000)) {
               cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of skin-treatment would you like to do? Choose the style of your liking.", skin)
            } else {
               cm.sendOk("Um... you don't have the skin-care coupon you need to receive the treatment. Sorry, but I am afraid we can't do it for you...")
               cm.dispose()
            }
         } else {
            cm.gainItem(5153000, (short) -1)
            cm.setSkin(selection)
            cm.sendOk("Enjoy your new and improved skin!")
            cm.dispose()
         }
      }
   }
}

NPC1012105 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012105(cm: cm))
   }
   return (NPC1012105) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }