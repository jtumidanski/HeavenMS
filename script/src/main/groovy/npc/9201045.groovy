package npc

import client.MapleCharacter
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleMap
import server.maps.MapleReactor
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201045 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean debug = false
   int curMap, stage

   def isAllGatesOpen() {
      MapleMap map = cm.getPlayer().getMap()

      for (int i = 0; i < 7; i++) {
         MapleReactor gate = map.getReactorByName("gate0" + i)
         if (gate.getState() != ((byte) 4)) {
            return false
         }
      }

      return true
   }

   static def clearStage(int stage, EventInstanceManager eim, int curMap) {
      eim.setProperty(stage + "stageclear", "true")

      eim.showClearEffect(true)
      eim.linkToNextStage(stage, "apq", curMap)  //opens the portal to the next map
   }

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 670010200) / 100).intValue() + 1
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()
         if (eim.getProperty(stage.toString() + "stageclear") != null) {
            if (stage < 5) {
               cm.sendNext("The portal is already open, advance for the trials that awaits you there.")
            } else if (stage == 5) {
               eim.warpEventTeamToMapSpawnPoint(670010700, 0)
            } else {
               if (cm.isEventLeader()) {
                  if (eim.getIntProperty("marriedGroup") == 0) {
                     eim.restartEventTimer(1 * 60 * 1000)
                     eim.warpEventTeam(670010800)
                  } else {
                     eim.setIntProperty("marriedGroup", 0)

                     eim.restartEventTimer(2 * 60 * 1000)
                     eim.warpEventTeamToMapSpawnPoint(670010750, 1)
                  }
               } else {
                  cm.sendNext("Wait for the leader's command to start the bonus phase.")
               }
            }
         } else {
            if (stage != 6) {
               if (eim.isEventLeader(cm.getPlayer())) {
                  int state = eim.getIntProperty("statusStg" + stage)

                  if (state == -1) {           // preamble
                     if (stage == 4) {
                        cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k of the Amorian Challenge. In this stage, collect me #b50 #t4031597##k from the mobs around here.")
                     } else if (stage == 5) {
                        cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k of the Amorian Challenge. That was quite the run to reach here, eh? Well, that was your task this stage here, anyway: survival! Firstly, have anyone alive gathered here before challenging the boss.")
                     }

                     int st = (debug) ? 2 : 0
                     eim.setProperty("statusStg" + stage, st)
                  } else {       // check stage completion
                     if (stage == 4) {
                        if (cm.haveItem(4031597, 50)) {
                           cm.gainItem(4031597, (short) -50)

                           long tl = eim.getTimeLeft()
                           if (tl >= 5 * 60 * 1000) {
                              eim.setProperty("timeLeft", tl.toString())
                              eim.restartEventTimer(4 * 60 * 1000)
                           }

                           cm.sendNext("Well done! Let me open the gate for you now.")
                           MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("9201045_TIME_RUNS_SHORT"))
                           clearStage(stage, eim, curMap)
                        } else {
                           cm.sendNext("Hey, didn't you pay heed? I demand #r50 #t4031597##k for the success of this trial.")
                        }

                     } else if (stage == 5) {
                        boolean pass = true

                        if (eim.isEventTeamTogether()) {
                           MapleCharacter[] party = cm.getEventInstance().getPlayers()
                           Rectangle area = cm.getMap().getArea(2)

                           for (int i = 0; i < party.size(); i++) {
                              MapleCharacter chr = party[i]

                              if (chr.isAlive() && !area.contains(chr.position())) {
                                 pass = false
                                 break
                              }
                           }
                        } else {
                           pass = false
                        }

                        if (pass) {
                           if (isAllGatesOpen()) {
                              String tl = eim.getProperty("timeLeft")
                              if (tl != null) {
                                 long tr = eim.getTimeLeft()

                                 Float tlf = (tl).toFloat()
                                 eim.restartEventTimer((long) tlf - (4 * 60 * 1000 - tr))
                              }

                              cm.sendNext("Okay, your team is already gathered. Talk to me when you guys feel ready to fight the #rGeist Balrog#k.")

                              MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("9201045_BOSS_FIGHT"))
                              clearStage(stage, eim, curMap)
                           } else {
                              cm.sendNext("You guys reached here by teleporting, eh? I can tell it. This is a shame, all gates needs to be open to fulfill this stage. If you still have the time, backtrack your steps and take down those gates.")
                           }
                        } else {
                           cm.sendNext("Your team has not gathered nearby yet. Give them some time to reach here.")
                        }
                     }
                  }
               } else {
                  cm.sendNext("Please tell your #bParty-Leader#k to come talk to me.")
               }
            } else {
               Rectangle area = cm.getMap().getArea(0)
               if (area.contains(cm.getPlayer().position())) {
                  if (cm.getPlayer().isAlive()) {
                     cm.warp(670010700, "st01")
                  } else {
                     cm.sendNext("Oy stand back... You are already dead.")
                  }
               } else {
                  if (cm.isEventLeader()) {
                     if (cm.haveItem(4031594, 1)) {
                        cm.gainItem(4031594, (short) -1)
                        cm.sendNext("Congratulations! Your party defeated the Geist Balrog, thus #bcompleting the Amorian Challenge#k! Talk to me again to start the bonus stage.")

                        clearStage(stage, eim, curMap)
                        eim.clearPQ()
                     } else {
                        cm.sendNext("How is it? Are you going to retrieve me the #b#t4031594##k? That's your last trial, hold on!")
                     }
                  } else {
                     cm.sendNext("Please tell your #bParty-Leader#k to come talk to me.")
                  }
               }
            }
         }

         cm.dispose()
      }
   }
}

NPC9201045 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201045(cm: cm))
   }
   return (NPC9201045) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }