package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Rain
	Map(s): 		Maple Road : Amherst (1010000)
	Description: 		Talks about Amherst
*/


class NPC12101 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("This is the town called #bAmherst#k, located at the northeast part of the Maple Island. You know that Maple Island is for beginners, right? I'm glad there are only weak monsters around this place.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && status == 2) {
            status -= 2
            start()
         } else if (mode == 0) {
            status -= 2
         } else {
            cm.dispose()
         }
      } else {
         if (status == 1) {
            cm.sendNextPrev("If you want to get stronger, then go to #bSouthperry#k where there's a harbor. Ride on the gigantic ship and head to the place called #bVictoria Island#k. It's incomparable in size compared to this tiny island.")
         } else if (status == 2) {
            cm.sendPrev("At the Victoria Island, you can choose your job. Is it called #bPerion#k...? I heard there's a bare, desolate town where warriors live. A highland...what kind of a place would that be?")
         } else if (status == 3) {
            cm.dispose()
         }
      }
   }
}

NPC12101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC12101(cm: cm))
   }
   return (NPC12101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }