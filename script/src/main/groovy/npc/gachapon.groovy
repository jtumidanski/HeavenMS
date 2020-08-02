package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NpcGachapon {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int ticketId = 5220000
   String[] mapName = ["Henesys", "Ellinia", "Perion", "Kerning City", "Sleepywood", "Mushroom Shrine", "Showa Spa (M)", "Showa Spa (F)", "Ludibrium", "New Leaf City", "El Nath", "Nautilus"]
   String curMapName = ""

   def start() {
      status = -1
      curMapName = mapName[(cm.getNpc() != 9100117 && cm.getNpc() != 9100109) ? (cm.getNpc() - 9100100) : cm.getNpc() == 9100109 ? 9 : 11]
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0 && mode == 1) {
            if (cm.haveItem(ticketId)) {
               cm.sendYesNo(I18nMessage.from("gachapon_USE_YOUR_TICKET").with(curMapName))
            } else {
               cm.sendSimple(I18nMessage.from("gachapon_WELCOME").with(curMapName))
            }
         } else if (status == 1 && cm.haveItem(ticketId)) {
            if (cm.canHold(1302000) && cm.canHold(2000000) && cm.canHold(3010001) && cm.canHold(4000000)) {
               // One free slot in every inventory.
               cm.gainItem(ticketId, (short) -1)
               cm.doGachapon()
            } else {
               cm.sendOk(I18nMessage.from("gachapon_NEED_INVENTORY_SPACE_FREE"))
            }
            cm.dispose()
         } else if (status == 1) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("gachapon_GACHAPON_DETAIL"))
            } else {
               cm.sendNext(I18nMessage.from("gachapon_TICKET_DETAIL"))
            }
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("gachapon_VARIETY_OF_ITEMS").with(curMapName, curMapName))
         } else {
            cm.dispose()
         }
      }
   }
}

NpcGachapon getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NpcGachapon(cm: cm))
   }
   return (NpcGachapon) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }