package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

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

      int lobby  // teams low level can be allocated at higher leveled lobbys
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
                  cm.sendOk("Your party completed such an astounding feat coming this far, #byou have defeated all the bosses#k, congratulations! Now I will be handing your reward as you are being transported out...")
               } else {
                  cm.sendOk("For #bdefeating all bosses#k in this instance, congratulations! You will now receive a prize that matches your performance here as I warp you out.")
               }
            } else if (state == 2) {
               if (cm.isEventLeader()) {
                  if (cm.getPlayer().getEventInstance().isEventTeamTogether()) {
                     cm.sendYesNo("Is your party ready to proceed to the next stages? Walk through the portal if you think you're done, the time is now.. Now, do you guys REALLY want to proceed?")
                  } else {
                     cm.sendOk("Please wait for your party to reassemble before proceeding.")
                     cm.dispose()
                  }
               } else {
                  cm.sendOk("Wait for your party leader to give me the signal to proceed. If you're not feeling too well and want to quit, walk through the portal and you will be transported out, and you will receive a prize for coming this far.")
                  cm.dispose()
               }
            } else if (state == 1) {
               cm.sendYesNo("Do you wish to abandon this instance?")
            } else {
               em = cm.getEventManager("BossRushPQ")
               if (em == null) {
                  cm.sendOk("The Boss Rush PQ has encountered an error.")
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple("#e#b<Party Quest: Boss Rush>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nWould you like to collaborate with party members to complete the expedition, or are you brave enough to take it on all by yourself? Have your #bparty leader#k talk to me or make yourself a party.#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.")
            }
         } else if (status == 1) {
            if (state == 3) {
               if (!cm.getPlayer().getEventInstance().giveEventReward(cm.getPlayer(), 6)) {
                  cm.sendOk("Please arrange a slot in all tabs of your inventory beforehand.")
                  cm.dispose()
                  return
               }

               cm.warp(970030000)
               cm.dispose()
            } else if (state == 2) {
               int restSpot = ((cm.getMapId() - 1) % 5) + 1
               cm.getPlayer().getEventInstance().restartEventTimer(restSpot * 4 * 60000)
               // adds (restspot number * 4) minutes
               cm.getPlayer().getEventInstance().warpEventTeam(970030100 + cm.getEventInstance().getIntProperty("lobby") + (500 * restSpot))

               cm.dispose()
            } else if (state == 1) {
               cm.warp(970030000)
               cm.dispose()
            } else {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk("You can participate in the party quest only if you are in a party.")
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk("Your party leader must talk to me to start this party quest.")
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
                           cm.sendOk("Another party has already entered the #rParty Quest#k in this channel. Please try another channel, or wait for the current party to finish.")
                        }
                     } else {
                        cm.sendOk("You cannot start this party quest yet, because either your party is not in the range size, some of your party members are not eligible to attempt it or they are not in this map. If you're having trouble finding party members, try Party Search.")
                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk("Your Party Search status is now: #b" + (psState ? "enabled" : "disabled") + "#k. Talk to me whenever you want to change it back.")
                  cm.dispose()
               } else {
                  cm.sendOk("#e#b<Party Quest: Boss Rush>#k#n\r\nBrave adventurers from all over the places travels here to test their skills and abilities in combat, as they face even more powerful bosses from MapleStory. Join forces with fellow adventurers or face all the burden by yourself and receive all the glory, it is up to you. REWARDS are given accordingly to how far the adventurers reach and extra prizes may are given to a random member of the party, all attributed at the end of an expedition.\r\n\r\nThis instance also supports #bmultiple lobbies for matchmaking several ranges of team levels#k at once: team up with players with lower level if you want better chances to swiftly set up a boss rush for your team.")
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