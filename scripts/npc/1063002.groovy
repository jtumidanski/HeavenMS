package npc

import client.inventory.MapleInventoryType
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1063002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[][] repeatablePrizes = [[4010006, 4], [4010007, 4], [4020007, 4]]

   def start() {
      if (cm.isQuestStarted(2054) && !cm.haveItem(4031028, 30)) {
         if (!cm.canHold(4031028, 30)) {
            cm.sendNext("Check for a available slot on your ETC inventory.")
            cm.dispose()
            return
         }

         cm.gainItem(4031028, (short) 30)
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

NPC1063002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1063002(cm: cm))
   }
   return (NPC1063002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }