package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int cost = 6000

   def start() {
      cm.sendYesNo("Hello, I'm in charge of selling tickets for the ship ride to Orbis Station of Ossyria. The ride to Orbis takes off every 10 minutes, beginning on the hour, and it'll cost you #b" + cost + " mesos#k. Are you sure you want to purchase #b#t4031045##k?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         }
         if (mode == 0) {
            cm.sendNext("You must have some business to take care of here, right?")
            cm.dispose()
            return
         }
         if (status == 1) {
            if (cm.getMeso() >= cost && cm.canHold(4031045)) {
               cm.gainItem(4031045, (short) 1)
               cm.gainMeso(-cost)
            } else {
               cm.sendOk("Are you sure you have #b" + cost + " mesos#k? If so, then I urge you to check your etc. inventory, and see if it's full or not.")
            }
            cm.dispose()
         }
      }
   }
}

NPC2040000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040000(cm: cm))
   }
   return (NPC2040000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }