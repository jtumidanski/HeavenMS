package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2131007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int exchangeItem = 4000438

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendSimple("These monsters are a piece of cake! One hit with my sword and I will kill them... better get a sword first.#b\r\n#L0#Hey, take these tree trunks. You can build a better sword with them.#l")
      } else if (status == 1) {
         if (!cm.haveItem(exchangeItem, 100)) {
            cm.sendNext("You don't have enough... I need at least 100.")
            cm.dispose()
         } else {
            double itemQuantity = cm.itemQuantity(exchangeItem) / 100
            cm.sendGetNumber("Hey, that's a good idea! I can give you #i4310000#Perfect Pitch for each 100 #i" + exchangeItem + "##t" + exchangeItem + "# you give me. How many do you want? (Current Items: " + cm.itemQuantity(exchangeItem) + ")", Math.min(300, itemQuantity).intValue(), 1, Math.min(300, itemQuantity).intValue())
         }
      } else if (status == 2) {
         if (selection >= 1 && selection <= cm.itemQuantity(exchangeItem) / 100) {
            if (!cm.canHold(4310000, selection)) {
               cm.sendOk("Please make some space in ETC tab.")
            } else {
               cm.gainItem(4310000, (short) selection)
               cm.gainItem(exchangeItem, (short) -(selection * 100))
               cm.sendOk("Thanks!")
            }
         }
         cm.dispose()
      }
   }
}

NPC2131007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2131007(cm: cm))
   }
   return (NPC2131007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }