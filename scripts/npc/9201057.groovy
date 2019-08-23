package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201057 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.c.getPlayer().getMapId() == 103000100 || cm.c.getPlayer().getMapId() == 600010001) {
         cm.sendYesNo("The ride to " + (cm.c.getPlayer().getMapId() == 103000100 ? "New Leaf City of Masteria" : "Kerning City of Victoria Island") + " takes off every minute, beginning on the hour, and it'll cost you #b5000 mesos#k. Are you sure you want to purchase #b#t" + (4031711 + (cm.c.getPlayer().getMapId() / 300000000).intValue()) + "##k?")
      } else if (cm.c.getPlayer().getMapId() == 600010002 || cm.c.getPlayer().getMapId() == 600010004) {
         cm.sendYesNo("Do you want to leave before the train start? There will be no refund.")
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode != 1) {
         cm.dispose()
         return
      }
      if (cm.c.getPlayer().getMapId() == 103000100 || cm.c.getPlayer().getMapId() == 600010001) {
         int item = 4031711 + (cm.c.getPlayer().getMapId() / 300000000).intValue()

         if (!cm.canHold(item)) {
            cm.sendNext("You don't have a etc. slot available.")
         } else if (cm.getMeso() >= 5000) {
            cm.gainMeso(-5000)
            cm.gainItem(item, (short) 1)
            cm.sendNext("There you go.")
         } else {
            cm.sendNext("You don't have enough mesos.")
         }
      } else {
         cm.warp(cm.c.getPlayer().getMapId() == 600010002 ? 600010001 : 103000100)
      }
      cm.dispose()
   }
}

NPC9201057 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201057(cm: cm))
   }
   return (NPC9201057) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }