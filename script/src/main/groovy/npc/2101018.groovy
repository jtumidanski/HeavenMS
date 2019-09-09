package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101018 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if ((cm.getPlayer().getLevel() < 19 || cm.getPlayer().getLevel() > 30) && !cm.getPlayer().isGM()) {
         cm.sendNext("You're not between level 20 and 30. Sorry, you may not participate.")
         cm.dispose()
         return
      }
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 4) {
         cm.getPlayer().saveLocation("MIRROR")
         cm.warp(980010000, 3)
         cm.dispose()
      }
      if (mode != 1) {
         if (mode == 0 && type == 0) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         cm.sendNext("I have prepared a huge festival here at Ariant for the great fighters of MapleStory. It's called #bThe Ariant Coliseum Challenge#k.")
      } else if (status == 1) {
         cm.sendNextPrev("The Ariant Coliseum Challenge is a competition that matches the skills of monster combat against others. In this competition, your object isn't to hunt the monster;  rather, you need to #beliminate a set amount of HP from the monster, followed by absorbing it with a jewel#k. #bThe fighter that ends up with the most jewels will win the competition.#k")
      } else if (status == 2) {
         cm.sendSimple("If you are a strong and brave warrior from #bPerion#k, training under Dances With Balrogs, then are you interested in participating in The Ariant Coliseum Challenge?!\r\n#b#L0# I'd love to participate in this great competition.#l")
      } else if (status == 3) {
         cm.sendNext("Okay, now I'll send you to the battle arena. I'd like to see you emerge victorious!")
      }
   }
}

NPC2101018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101018(cm: cm))
   }
   return (NPC2101018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }