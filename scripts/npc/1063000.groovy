package npc

import client.inventory.MapleInventoryType
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Flower pile
	Map(s): 		
	Description: 	John JQ
*/


class NPC1063000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[][] repeatablePrizes = [[4010000, 3], [4010001, 3], [4010002, 3], [4010003, 3], [4010004, 3], [4010005, 3]]

   def start() {
      if (cm.isQuestStarted(2052) && !cm.haveItem(4031025, 10)) {
         if (!cm.canHold(4031025, 10)) {
            cm.sendNext("Check for a available slot on your ETC inventory.")
            cm.dispose()
            return
         }

         cm.gainItem(4031025, (short) 10)
      } else {
         if (cm.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
            cm.sendNext("Check for a available slot on your ETC inventory.")
            cm.dispose()
            return
         }

         int[] itemPrize = repeatablePrizes[Math.floor((Math.random() * repeatablePrizes.length)).intValue()]
         cm.gainItem(itemPrize[0], (short) itemPrize[1])
      }

      cm.warp(105040300, 0)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1063000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1063000(cm: cm))
   }
   return (NPC1063000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }