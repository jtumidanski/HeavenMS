package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] towns = [100000000, 101000000, 102000000, 103000000, 104000000]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendNext("Aye...are you scared of speed or heights? You can't trust my flying skills? Trust me, I've worked out all the kinks!")
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendNext("I don't know how you found out about this, but you came to the right place! For those that wandered around Nihal Desert and are getting homesick, I am offering a flight straight to Victoria Island, non-stop! Don't worry about the flying ship--it's only fallen once or twice! Don't you feel claustrophobic being in a long flight on that small ship?")
         } else if (status == 1) {
            cm.sendYesNo("Please remember two things. One, this line is actually for overseas shipping, so #rI cannot guarantee exactly which town you'll land#k. Two, since I am putting you in this special flight, it'll be a bit expensive. The service charge is #e#b10,000 mesos#n#k. There's a flight that is about to take off. Are you interested in this direct flight?")
         } else if (status == 2) {
            cm.sendNext("Okay, ready to takeoff~")
         } else if (status == 3) {
            if (cm.getMeso() >= 10000) {
               cm.gainMeso(-10000)
               cm.warp(towns[Math.floor(Math.random() * towns.length).intValue()])
            } else {
               cm.sendNextPrev("Hey, are you short on cash? I told you you'll need #b10,000#k mesos to get on this.")
               cm.dispose()
            }
         }
      }
   }
}

NPC2101013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101013(cm: cm))
   }
   return (NPC2101013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }