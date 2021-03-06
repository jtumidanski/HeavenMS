package npc

import client.inventory.Item
import client.inventory.MaplePet
import constants.MapleInventoryType
import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPCWaterOfLife {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   List<MaplePet> dList

   def start() {
      status = -1

      dList = cm.getDriedPets()
      if (dList.size() == 0) {
         MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("WATER_OF_LIFE_NOT_NECESSARY"))
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
            cm.sendYesNo(I18nMessage.from("waterOfLife_I_AM_MAR"))


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
               cm.sendNext(I18nMessage.from("waterOfLife_REAWAKEN"))


               Item it = cm.getPlayer().getInventory(MapleInventoryType.CASH).getItem(sPet.position())
               it = it.expiration(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 90))
               cm.getPlayer().forceUpdateItem(it)

               cm.gainItem(5180000, (short) -1)
            } else {
               cm.sendNext(I18nMessage.from("waterOfLife_GOOD_BYE"))

            }

            cm.dispose()
         }
      }
   }
}

NPCWaterOfLife getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPCWaterOfLife(cm: cm))
   }
   return (NPCWaterOfLife) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }