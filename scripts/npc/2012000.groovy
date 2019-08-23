package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012000 {
   NPCConversationManager cm
   int status = 0
   int select = -1

   int[] ticket = [4031047, 4031074, 4031331, 4031576]
   int[] cost = [5000, 6000, 30000, 6000]
   String[] mapNames = ["Ellinia of Victoria Island", "Ludibrium", "Leafre", "Ariant"]
   String[] mapName2 = ["Ellinia of Victoria Island", "Ludibrium", "Leafre of Minar Forest", "Nihal Desert"]

   def start() {
      String where = "Hello, I'm in charge of selling tickets for the ship ride for every destination. Which ticket would you like to purchase?"
      for (int i = 0; i < ticket.length; i++) {
         where += "\r\n#L" + i + "##b" + mapNames[i] + "#k#l"
      }
      cm.sendSimple(where)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            select = selection
            cm.sendYesNo("The ride to " + mapName2[select] + " takes off every " + (select == 0 ? 15 : 10) + " minutes, beginning on the hour, and it'll cost you #b" + cost[select] + " mesos#k. Are you sure you want to purchase #b#t" + ticket[select] + "##k?")
         } else if (status == 2) {
            if (cm.getMeso() < cost[select] || !cm.canHold(ticket[select])) {
               cm.sendOk("Are you sure you have #b" + cost[select] + " mesos#k? If so, then I urge you to check you etc. inventory, and see if it's full or not.")
            } else {
               cm.gainMeso(-cost[select])
               cm.gainItem(ticket[select], (short) 1)
            }
            cm.dispose()
         }
      }
   }
}

NPC2012000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012000(cm: cm))
   }
   return (NPC2012000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }