package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Door of Dimension
	Map(s): 		
	Description: 	Enter 3rd job event
*/


class NPC1061009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (canEnterDimensionMap(cm.getMapId(), cm.getJob().getId()) && cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)) {
         String js = jobString(cm.getPlayer().getJob().getJobNiche())

         EventManager em = cm.getEventManager("3rdJob_" + js)
         if (em == null) {
            cm.sendOk(I18nMessage.from("1061009_CLOSED").with(js))
         } else {
            if (!em.startInstance(cm.getPlayer())) {
               cm.sendOk(I18nMessage.from("1061009_ALREADY_CHALLENGING"))
            }

            cm.dispose()
            return
         }
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }

   static def jobString(int niche) {
      if (niche == 1) {
         return "warrior"
      } else if (niche == 2) {
         return "magician"
      } else if (niche == 3) {
         return "bowman"
      } else if (niche == 4) {
         return "thief"
      } else if (niche == 5) {
         return "pirate"
      }

      return "beginner"
   }

   static def canEnterDimensionMap(int mapId, int jobId) {
      if (mapId == 105070001 && (jobId >= 110 && jobId <= 130)) {
         return true
      } else if (mapId == 105040305 && (jobId >= 310 && jobId <= 320)) {
         return true
      } else if (mapId == 100040106 && (jobId >= 210 && jobId <= 230)) {
         return true
      } else if (mapId == 107000402 && (jobId >= 410 && jobId <= 420)) {
         return true
      } else if (mapId == 105070200 && (jobId >= 510 && jobId <= 520)) {
         return true
      }

      return false
   }
}

NPC1061009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061009(cm: cm))
   }
   return (NPC1061009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }