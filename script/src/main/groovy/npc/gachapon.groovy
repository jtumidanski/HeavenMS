package npc


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
               cm.sendYesNo("You may use the " + curMapName + " Gachapon. Would you like to use your Gachapon ticket?")
            } else {
               cm.sendSimple("Welcome to the " + curMapName + " Gachapon. How may I help you?\r\n\r\n#L0#What is Gachapon?#l\r\n#L1#Where can you buy Gachapon tickets?#l")
            }
         } else if (status == 1 && cm.haveItem(ticketId)) {
            if (cm.canHold(1302000) && cm.canHold(2000000) && cm.canHold(3010001) && cm.canHold(4000000)) {
               // One free slot in every inventory.
               cm.gainItem(ticketId, (short) -1)
               cm.doGachapon()
            } else {
               cm.sendOk("Please have at least one slot in your #rEQUIP, USE, SET-UP, #kand #rETC#k inventories free.")
            }
            cm.dispose()
         } else if (status == 1) {
            if (selection == 0) {
               cm.sendNext("Play Gachapon to earn rare scrolls, equipment, chairs, mastery books, and other cool items! All you need is a #bGachapon Ticket#k to be the winner of a random mix of items.")
            } else {
               cm.sendNext("Gachapon Tickets are available in the #rCash Shop#k and can be purchased using NX or Maple Points. Click on the red SHOP at the lower right hand corner of the screen to visit the #rCash Shop#k where you can purchase tickets.")
            }
         } else if (status == 2) {
            cm.sendNextPrev("You'll find a variety of items from the " + curMapName + " Gachapon, but you'll most likely find items and scrolls related to " + curMapName + ".")
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