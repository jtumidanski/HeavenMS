package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Weaver
	Map(s): 		Ludibrium : Ludibrium Pet Walkway
	Description: 	
*/


class NPC2040032 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo("This is the road where you can go take a walk with your pet. You can walk around with it, or you can train your pet to go through obstacles here. If you aren't too close with your pet yet, that may present a problem and he will not follow your command as much... So, what do you think? Wanna train your pet?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         cm.sendNext("Hmmm ... too busy to do it right now? If you feel like doing it, though, come back and find me.")
      } else if (mode == 1) {
         if (cm.haveItem(4031128)) {
            cm.sendNext("Get that letter, jump over obstacles with your pet, and take that letter to my brother Trainer Frod. Give him the letter and something good is going to happen to your pet.")
         } else {
            cm.gainItem(4031128, (short) 1)
            cm.sendOk("Ok, here's the letter. He wouldn't know I sent you if you just went there straight, so go through the obstacles with your pet, go to the very top, and then talk to Trainer Frod to give him the letter. It won't be hard if you pay attention to your pet while going through obstacles. Good luck!")
         }
      }
      cm.dispose()
   }
}

NPC2040032 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040032(cm: cm))
   }
   return (NPC2040032) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }