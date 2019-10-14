package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int stage

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
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()
         if (eim == null) {
            cm.warp(990001100)
         } else {
            if (eim.getProperty("stage1clear") == "true") {
               cm.sendOk("Excellent work. You may proceed to the next stage.")
               cm.dispose()
               return
            }

            if (cm.isEventLeader()) {
               if (status == 0) {
                  if (eim.getProperty("stage1status") == null || eim.getProperty("stage1status") == "waiting") {
                     if (eim.getProperty("stage1phase") == null) {
                        stage = 1
                        eim.setProperty("stage1phase", stage)
                     } else {
                        stage = (eim.getProperty("stage1phase")).toInteger()
                     }

                     if (stage == 1) {
                        cm.sendOk("In this challenge, I shall show a pattern on the statues around me. When I give the word, repeat the pattern to me to proceed.")
                     } else {
                        cm.sendOk("I shall now present a more difficult puzzle for you. Good luck.")
                     }
                  } else if (eim.getProperty("stage1status") == "active") {
                     stage = (eim.getProperty("stage1phase")).toInteger()

                     if (eim.getProperty("stage1combo") == eim.getProperty("stage1guess")) {
                        if (stage == 3) {
                           cm.getPlayer().getMap().getReactorByName("statuegate").forceHitReactor((byte) 1)
                           clearStage(1, eim)
                           cm.getGuild().gainGP(15)

                           cm.sendOk("Excellent work. You may proceed to the next stage.")
                        } else {
                           cm.sendOk("Very good. You still have more to complete, however. Talk to me again when you're ready.")
                           eim.setProperty("stage1phase", stage + 1)
                           MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "You have completed part " + stage + " of the Gatekeeper Test.")
                        }

                     } else {
                        eim.showWrongEffect()
                        cm.sendOk("You have failed this test.")
                        MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "You have failed the Gatekeeper Test.")
                        eim.setProperty("stage1phase", "1")
                     }
                     eim.setProperty("stage1status", "waiting")
                     cm.dispose()
                  } else {
                     cm.sendOk("The statues are working on the pattern. Please wait.")
                     cm.dispose()
                  }
               } else if (status == 1) {
                  int[] reactors = getReactors()
                  int[] combo = makeCombo(reactors)
                  MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Please wait while the combination is revealed.")
                  int delay = 5000
                  for (int i = 0; i < combo.length; i++) {
                     cm.getPlayer().getMap().getReactorByOid(combo[i]).delayedHitReactor(cm.getClient(), delay + 3500 * i)
                  }
                  eim.setProperty("stage1status", "display")
                  eim.setProperty("stage1combo", "")
                  cm.dispose()
               }
            } else {
               cm.sendOk("I need the leader of this instance to speak with me, nobody else.")
               cm.dispose()
            }
         }
      }
   }

   def getReactors() {
      int[] reactors = []

      Iterator<MapleReactor> iter = cm.getPlayer().getMap().getReactors().iterator() as Iterator<MapleReactor>
      while (iter.hasNext()) {
         MapleReactor mo = iter.next()
         if (mo.getName() != "statuegate") {
            reactors << mo.objectId()
         }
      }

      return reactors
   }

   def makeCombo(int[] reactors) {
      int[] combo = []
      while (combo.length < (stage + 3)) {
         int chosenReactor = reactors[Math.floor(Math.random() * reactors.length).intValue()]
         boolean repeat = false
         if (combo.length > 0) {
            for (int i = 0; i < combo.length; i++) {
               if (combo[i] == chosenReactor) {
                  repeat = true
                  break
               }
            }
         }
         if (!repeat) {
            combo << chosenReactor
         }
      }
      return combo
   }
}

NPC9040009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040009(cm: cm))
   }
   return (NPC9040009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }