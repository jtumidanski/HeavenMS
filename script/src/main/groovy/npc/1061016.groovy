package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1061016 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] itemIds = [2040728, 2040729, 2040730, 2040731, 2040732, 2040733, 2040734, 2040735, 2040736, 2040737, 2040738, 2040739]

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode != 1) {
         cm.dispose()
         return
      }
      status++
      if (status == 0) {
         cm.sendSimple("Hello, #h0#. I can exchange your Balrog Leathers.\r\n\r\n#r#L1#Redeem items#l#k")
      } else if (status == 1) {
         String selStr = "Well, okay. These are what you can redeem...\r\n\r\n#b"
         for (def i = 0; i < itemIds.length; i++) {
            selStr += "#L" + i + "##i" + itemIds[i] + "##z" + itemIds[i] + "##l\r\n"
         }
         cm.sendSimple(selStr)
      } else if (status == 2) {
         if (!cm.canHold(itemIds[selection], 1)) {
            cm.sendOk("Please make room")
         } else if (!cm.haveItemWithId(4001261)) {
            cm.sendOk("You don't have enough leathers.")
         } else {
            cm.gainItem(4001261, (short) -1)
            cm.gainItem(itemIds[selection], (short) 1)
            cm.sendOk("Thank you for your redemption")
         }
         cm.dispose()
      }
   }
}

NPC1061016 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061016(cm: cm))
   }
   return (NPC1061016) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }