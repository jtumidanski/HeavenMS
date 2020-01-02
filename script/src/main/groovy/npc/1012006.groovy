package npc


import scripting.npc.NPCConversationManager

class NPC1012006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple("Do you have any business with me?\r\n#L0##bPlease tell me about this place.#l\r\n#L1#I'm here through a word from Mar the Fairy...#k#l")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.sendNext("Hmmm ... too busy to do it right now? If you feel like doing it, though, come back and find me.")
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               if (cm.haveItem(4031035)) {
                  cm.sendNext("Get that letter, jump over obstacles with your pet, and take that letter to my brother Trainer Frod. Give him the letter and something good is going to happen to your pet.")
                  cm.dispose()
               } else {
                  cm.sendYesNo("This is the road where you can go take a walk with your pet. You can just walk around with it, or you can train your pet to go through the obstacles here. If you aren't too close with your pet yet, that may present a problem and he will not follow your command as much... So, what do you think? Wanna train your pet?")
               }
            } else {
               cm.sendOk("Hey, are you sure you've met #bMar the Fairy#k? Don't lie to me if you've never met her before because it's obvious. That wasn't even a good lie!!")
               cm.dispose()
            }
         } else if (status == 2) {
            cm.gainItem(4031035, (short) 1)
            cm.sendNext("Ok, here's the letter. He wouldn't know I sent you if you just went there straight, so go through the obstacles with your pet, go to the very top, and then talk to Trainer Frod to give him the letter. It won't be hard if you pay attention to your pet while going through obstacles. Good luck!")
            cm.dispose()
         }
      }
   }
}

NPC1012006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012006(cm: cm))
   }
   return (NPC1012006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }