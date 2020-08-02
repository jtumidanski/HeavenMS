package npc
import tools.I18nMessage

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPCrank_user {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            MaplePlayerNPC playerNPC = cm.getPlayerNPCByScriptId(cm.getNpc())

            if (playerNPC != null) {
               String branchJobName = GameConstants.getJobName(playerNPC.getJob())

               String rankStr = "Hi, I am #b" + playerNPC.getName() + "#k, #r" + GameConstants.ordinal(playerNPC.getWorldJobRank()) + "#k in the #r" + branchJobName + "#k class to reach the max level and obtain a statue on " + GameConstants.WORLD_NAMES[cm.getPlayer().getWorld()] + ".\r\n"
               rankStr += "\r\n    World rank: #e#b" + GameConstants.ordinal(playerNPC.getWorldRank()) + "#k#n"
               rankStr += "\r\n    Overall " + branchJobName + " rank: #e#b" + GameConstants.ordinal(playerNPC.getOverallJobRank()) + "#k#n"
               rankStr += "\r\n    Overall rank: #e#b" + GameConstants.ordinal(playerNPC.getOverallRank()) + "#k#n"

               cm.sendOk(rankStr)
            } else {
               cm.sendOk(I18nMessage.from("rank_user_HOW_ARE_YOU_DOING"))
            }

            cm.dispose()
         }
      }
   }
}

NPCrank_user getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPCrank_user(cm: cm))
   }
   return (NPCrank_user) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }