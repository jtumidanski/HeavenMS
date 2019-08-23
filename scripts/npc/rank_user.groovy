package npc

import constants.GameConstants
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
            MaplePlayerNPC pnpc = cm.getPlayerNPCByScriptid(cm.getNpc())

            if (pnpc != null) {
               String branchJobName = GameConstants.getJobName(pnpc.getJob())

               String rankStr = "Hi, I am #b" + pnpc.getName() + "#k, #r" + GameConstants.ordinal(pnpc.getWorldJobRank()) + "#k in the #r" + branchJobName + "#k class to reach the max level and obtain a statue on " + GameConstants.WORLD_NAMES[cm.getPlayer().getWorld()] + ".\r\n"
               rankStr += "\r\n    World rank: #e#b" + GameConstants.ordinal(pnpc.getWorldRank()) + "#k#n"
               rankStr += "\r\n    Overall " + branchJobName + " rank: #e#b" + GameConstants.ordinal(pnpc.getOverallJobRank()) + "#k#n"
               rankStr += "\r\n    Overall rank: #e#b" + GameConstants.ordinal(pnpc.getOverallRank()) + "#k#n"

               cm.sendOk(rankStr)
            } else {
               cm.sendOk("Hi, how're you doing?")
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