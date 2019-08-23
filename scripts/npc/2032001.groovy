package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Spiruna
	Map(s): 		Orbis : Old Man's House
	Description: 	Refining NPC
*/


class NPC2032001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      if (cm.isQuestCompleted(3034)) {
         cm.sendYesNo("You've been so much of a help to me... If you have any Dark Crystal Ore, I can refine it for you for only #b500000 meso#k each.")
      } else {
         cm.sendOk("Go away, I'm trying to meditate.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
         return
      }
      status++
      if (status == 1) {
         cm.sendGetNumber("Okay, so how many do you want me to make?", 1, 1, 100)
      } else if (status == 2) {
         boolean complete = true

         if (cm.getMeso() < 500000 * selection) {
            cm.sendOk("I'm sorry, but I am NOT doing this for free.")
            cm.dispose()
            return
         } else if (!cm.haveItem(4004004, 10 * selection)) {
            complete = false
         } else if (!cm.canHold(4005004, selection)) {
            cm.sendOk("Are you having trouble with no empty slots on your inventory? Sort that out first!")
            cm.dispose()
            return
         }
         if (!complete) {
            cm.sendOk("I need that ore to refine the Crystal. No exceptions..")
         } else {
            cm.gainItem(4004004, (short) (-10 * selection))
            cm.gainMeso(-500000 * selection)
            cm.gainItem(4005004, (short) selection)
            cm.sendOk("Use it wisely.")
         }
         cm.dispose()
      }
   }
}

NPC2032001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2032001(cm: cm))
   }
   return (NPC2032001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }