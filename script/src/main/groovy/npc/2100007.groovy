package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2100007 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

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
            cm.sendNext("Hohoh~ welcome welcome. Welcome to Ariant Skin Care. You have stepped into a renowned Skin Care shop that even the Queen herself frequents this place. If you have #bAriant skin care coupon#k with you, we'll take care of the rest. How about letting work on your skin today?")
         } else if (status == 1) {
            cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of skin-treatment would you like to do? Choose the style of your liking...", skin)
         } else if (status == 2) {
            cm.dispose()
            if (cm.haveItem(5153007)) {
               cm.gainItem(5153007, (short) -1)
               cm.setSkin(skin[selection])
               cm.sendOk("Enjoy your new and improved skin!")
            } else {
               cm.sendNext("Hmmm... I don't think you have our Skin Care coupon with you. Without it, I can't give you the treatment")
            }
         }
      }
   }
}

NPC2100007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2100007(cm: cm))
   }
   return (NPC2100007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }