package npc

import client.inventory.Item
import client.inventory.MapleInventoryType
import client.inventory.MaplePet
import scripting.npc.NPCConversationManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPCwaterOfLife {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   List<MaplePet> dList

   def start() {
      status = -1

      dList = cm.getDriedPets()
      if (dList.size() == 0) {
         MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "You currently do not own a pet that needs to be treated with Water of Life.")
         cm.dispose()
         return
      }

      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo("I am Mar the Fairy. You have the #bWater of Life#k... With this, I can bring a doll back to life with my magic. What do you think? Do you want to use this item and reawaken your pet ...?")

         } else if (status == 1) {
            String talkStr = "So which pet you want to reawaken? Please choose the pet you'd most like to reawaken...\r\n\r\n"

            String listStr = ""
            int i = 0

            Iterator<MaplePet> dIter = dList.iterator()
            while (dIter.hasNext()) {
               MaplePet dPet = dIter.next()

               listStr += "#b#L" + i + "# " + dPet.name() + " #k - Lv " + dPet.level() + " Closeness " + dPet.closeness()
               listStr += "#l\r\n"

               i++
            }

            cm.sendSimple(talkStr + listStr)
         } else if (status == 2) {
            MaplePet sPet = dList.get(selection)

            if (sPet != null) {
               cm.sendNext("Your doll has now reawaken as your pet! However, my magic isn't perfect, so I can't promise an eternal life for your pet... Please take care of that pet before the Water of Life dries. Well then, good bye...")

               Item it = cm.getPlayer().getInventory(MapleInventoryType.CASH).getItem(sPet.position())
               it.expiration_(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 90))
               cm.getPlayer().forceUpdateItem(it)

               cm.gainItem(5180000, (short) -1)
            } else {
               cm.sendNext("Oh, well then. Good bye...")
            }

            cm.dispose()
         }
      }
   }
}

NPCwaterOfLife getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPCwaterOfLife(cm: cm))
   }
   return (NPCwaterOfLife) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }