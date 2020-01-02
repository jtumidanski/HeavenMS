package npc


import scripting.npc.NPCConversationManager
import server.partyquest.AriantColiseum

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101016 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   AriantColiseum arena

   def start() {
      arena = cm.getPlayer().getAriantColiseum()
      if (arena == null) {
         cm.sendOk("Hey, I did not see you on the field during the battle in the arena! What are you doing here?")
         cm.dispose()
         return
      }

      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            int ariantScore = arena.getAriantScore(cm.getPlayer())
            if (ariantScore < 1 && !cm.getPlayer().isGM()) {
               cm.sendOk("Too bad, you did not get any jewelry!")
               cm.dispose()
            } else {
               cm.sendNext("Ok, let's see... You did very well and you brought #b" + ariantScore + "#k jewelry that I love. Since you have completed the match, I will reward you with a Battle Arena score of #b" + arena.getAriantRewardTier(cm.getPlayer()) + " points#k. If you want to know more about the Battle Arena score, then talk to #b#p2101015##k.")
            }
         } else if (status == 1) {
            //cm.warp(980010020, 0);
            int rewardTier = arena.getAriantRewardTier(cm.getPlayer())
            arena.clearAriantRewardTier(cm.getPlayer())
            arena.clearAriantScore(cm.getPlayer())
            cm.removeAll(4031868)

            cm.getPlayer().gainExp((int) (92.7 * cm.getPlayer().getExpRate() * rewardTier), true, true)
            cm.getPlayer().gainAriantPoints(rewardTier)
            cm.sendOk("Alright! Make me more jewels next time! Ahahahahah!")
            cm.dispose()
         }
      }
   }
}

NPC2101016 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101016(cm: cm))
   }
   return (NPC2101016) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }