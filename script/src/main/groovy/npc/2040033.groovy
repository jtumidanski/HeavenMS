package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Neru
	Map(s): 		Ludibrium : Ludibrium Pet Walkway
	Description: 	
*/


class NPC2040033 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031128)) {
         cm.sendNext("Eh, that's my brother's letter! Probably scolding me for thinking I'm not working and stuff...Eh? Ahhh...you followed my brother's advice and trained your pet and got up here, huh? Nice!! Since you worked hard to get here, I'll boost your intimacy level with your pet.")
      } else {
         cm.sendOk("My brother told me to take care of the pet obstacle course, but ... since I'm so far away from him, I can't help but wanting to goof around ...hehe, since I don't see him in sight, might as well just chill for a few minutes.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode >= 1) {
         if (cm.getPlayer().getNoPets() == 0) {
            cm.sendNextPrev("Hmmm ... did you really get here with your pet? These obstacles are for pets. What are you here for without it?? Get outta here!")
         } else {
            cm.gainItem(4031128, (short) -1)
            cm.gainCloseness(4)
            cm.sendNextPrev("What do you think? Don't you think you have gotten much closer with your pet? If you have time, train your pet again on this obstacle course...of course, with my brother's permission.")
         }
      }
      cm.dispose()
   }
}

NPC2040033 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040033(cm: cm))
   }
   return (NPC2040033) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }