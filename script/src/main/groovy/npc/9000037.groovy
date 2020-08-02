package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000037 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state
   EventManager em

   def onRestingSpot() {
      return cm.getMapId() >= 970030001 && cm.getMapId() <= 970030010
   }

   def isFinalBossDone() {
      return cm.getMapId() >= 970032700 && cm.getMapId() < 970032800 && cm.getMap().getMonsters().isEmpty()
   }

   static def detectTeamLobby(MaplePartyCharacter[] team) {
      int midLevel = 0

      for (int i = 0; i < team.size(); i++) {
         MaplePartyCharacter player = team[i]
         midLevel += player.getLevel()
      }
      midLevel = Math.floor(midLevel / team.size()).intValue()

      int lobby  // teams low level can be allocated at higher leveled lobbies
      if (midLevel <= 20) {
         lobby = 0
      } else if (midLevel <= 40) {
         lobby = 1
      } else if (midLevel <= 60) {
         lobby = 2
      } else if (midLevel <= 80) {
         lobby = 3
      } else if (midLevel <= 90) {
         lobby = 4
      } else if (midLevel <= 100) {
         lobby = 5
      } else if (midLevel <= 110) {
         lobby = 6
      } else {
         lobby = 7
      }

      return lobby
   }

   def start() {
      status = -1
      state = (cm.getMapId() >= 970030001 && cm.getMapId() <= 970042711) ? (!onRestingSpot() ? (isFinalBossDone() ? 3 : 1) : 2) : 0
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
            if (state == 3) {
               if (cm.getEventInstance().getProperty("clear") == null) {
                  cm.getEventInstance().clearPQ()
                  cm.getEventInstance().setProperty("clear", "true")
               }

               if (cm.isEventLeader()) {
                  cm.sendOk(I18nMessage.from("9000037_ASTOUNDING_FEAT"))

               } else {
                  cm.sendOk(I18nMessage.from("9000037_PRIZE"))

               }
            } else if (state == 2) {
               if (cm.isEventLeader()) {
                  if (cm.getPlayer().getEventInstance().isEventTeamTogether()) {
                     cm.sendYesNo(I18nMessage.from("9000037_READY_TO_PROCEED"))

                  } else {
                     cm.sendOk(I18nMessage.from("9000037_PLEASE_WAIT"))

                     cm.dispose()
                  }
               } else {
                  cm.sendOk(I18nMessage.from("9000037_PARTY_LEADER_SIGNAL"))

                  cm.dispose()
               }
            } else if (state == 1) {
               cm.sendYesNo(I18nMessage.from("9000037_ABANDON"))

            } else {
               em = cm.getEventManager("BossRushPQ")
               if (em == null) {
                  cm.sendOk(I18nMessage.from("9000037_ENCOUNTERED_ERROR"))

                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple(I18nMessage.from("9000037_PARTY_QUEST_INFO").with(em.getProperty("party"), cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable"))

            }
         } else if (status == 1) {
            if (state == 3) {
               if (!cm.getPlayer().getEventInstance().giveEventReward(cm.getPlayer(), 6)) {
                  cm.sendOk(I18nMessage.from("9000037_ARRANGE_SLOT"))

                  cm.dispose()
                  return
               }

               cm.warp(970030000)
               cm.dispose()
            } else if (state == 2) {
               int restSpot = ((cm.getMapId() - 1) % 5) + 1
               cm.getPlayer().getEventInstance().restartEventTimer(restSpot * 4 * 60000)
               // adds (restSpot number * 4) minutes
               cm.getPlayer().getEventInstance().warpEventTeam(970030100 + cm.getEventInstance().getIntProperty("lobby") + (500 * restSpot))

               cm.dispose()
            } else if (state == 1) {
               cm.warp(970030000)
               cm.dispose()
            } else {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk(I18nMessage.from("9000037_MUST_BE_IN_PARTY"))

                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk(I18nMessage.from("9000037_PARTY_LEADER_MUST_TALK"))

                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        int lobby = detectTeamLobby(eli), i
                        for (i = lobby; i < 8; i++) {
                           if (em.startInstance(i, cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                              break
                           }
                        }

                        if (i == 8) {
                           cm.sendOk(I18nMessage.from("9000037_ANOTHER_PARTY"))

                        }
                     } else {
                        cm.sendOk(I18nMessage.from("9000037_PARTY_REQUIREMENTS"))

                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk(I18nMessage.from("9000037_PARTY_SEARCH_STATUS").with((psState ? "enabled" : "disabled")))

                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9000037_PARTY_QUEST_INFO_2"))

                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC9000037 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000037(cm: cm))
   }
   return (NPC9000037) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }