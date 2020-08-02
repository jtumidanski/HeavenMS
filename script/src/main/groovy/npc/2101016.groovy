package npc
import tools.I18nMessage


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
         cm.sendOk(I18nMessage.from("2101016_WHAT_ARE_YOU_DOING_HERE"))
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
               cm.sendOk(I18nMessage.from("2101016_NO_JEWELRY"))
               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("2101016_SCORE_REWARD").with(ariantScore, arena.getAriantRewardTier(cm.getPlayer())))
            }
         } else if (status == 1) {
            //cm.warp(980010020, 0);
            int rewardTier = arena.getAriantRewardTier(cm.getPlayer())
            arena.clearAriantRewardTier(cm.getPlayer())
            arena.clearAriantScore(cm.getPlayer())
            cm.removeAll(4031868)

            cm.getPlayer().gainExp((int) (92.7 * cm.getPlayer().getExpRate() * rewardTier), true, true)
            cm.getPlayer().gainAriantPoints(rewardTier)
            cm.sendOk(I18nMessage.from("2101016_MAKE_MORE_JEWELS"))
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