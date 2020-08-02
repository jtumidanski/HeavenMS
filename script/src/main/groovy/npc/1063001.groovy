package npc

import client.inventory.MapleInventoryType
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		John JQ Flower pile #2
	Map(s): 		
	Description: 	
*/


class NPC1063001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[][] repeatablePrizes = [[4020000, 4], [4020002, 4], [4020006, 4]]

   def start() {
      if (cm.isQuestStarted(2053) && !cm.haveItem(4031026, 20)) {
         if (!cm.canHold(4031026, 20)) {
            cm.sendNext(I18nMessage.from("1063001_CHECK_YOUR_ETC_INVENTORY"))
            cm.dispose()
            return
         }

         cm.gainItem(4031026, (short) 20)
      } else {
         if (cm.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
            cm.sendNext(I18nMessage.from("1063001_CHECK_YOUR_ETC_INVENTORY"))
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

NPC1063001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1063001(cm: cm))
   }
   return (NPC1063001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }