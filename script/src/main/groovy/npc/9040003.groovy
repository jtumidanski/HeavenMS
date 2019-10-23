package npc

import net.server.processor.MapleGuildProcessor
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def clearStage(int stage, EventInstanceManager eim) {
      eim.setProperty("stage" + stage + "clear", "true")
      eim.showClearEffect(true)

      eim.giveEventPlayersStageReward(stage)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            cm.dispose()
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()

         if (eim.getProperty("stage4clear") != null && eim.getProperty("stage4clear") == "true") {
            cm.sendOk("After what I thought would be an immortal sleep, I have finally found someone that will save Sharenian. I can truly rest in peace now.")
            cm.dispose()
            return
         }

         if (status == 0) {
            if (cm.isEventLeader()) {
               cm.sendNext("After what I thought would be an immortal sleep, I have finally found someone that will save Sharenian. This old man will now pave the way for you to finish the quest.")

               clearStage(4, eim)
               MapleGuildProcessor.getInstance().gainGP(cm.getGuild(), 30)
               cm.getPlayer().getMap().getReactorByName("ghostgate").forceHitReactor((byte) 1)

               cm.dispose()
            } else {
               cm.sendOk("I need the leader of your party to speak with me, nobody else.")
               cm.dispose()
            }
         }
      }
   }
}

NPC9040003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040003(cm: cm))
   }
   return (NPC9040003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }