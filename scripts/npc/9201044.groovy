package npc

import client.MapleCharacter
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201044 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean debug = false
   boolean autopass = false

   def spawnMobs(maxSpawn) {
      int[] spawnPosX
      int[] spawnPosY

      MapleMap mapObj = cm.getMap()
      if (stage == 2) {
         spawnPosX = [619, 299, 47, -140, -471]
         spawnPosY = [-840, -840, -840, -840, -840]

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
               MapleMonster mobObj1 = MapleLifeFactory.getMonster(9400515)
               MapleMonster mobObj2 = MapleLifeFactory.getMonster(9400516)
               MapleMonster mobObj3 = MapleLifeFactory.getMonster(9400517)

               mapObj.spawnMonsterOnGroundBelow(mobObj1, new Point(spawnPosX[i], spawnPosY[i]))
               mapObj.spawnMonsterOnGroundBelow(mobObj2, new Point(spawnPosX[i], spawnPosY[i]))
               mapObj.spawnMonsterOnGroundBelow(mobObj3, new Point(spawnPosX[i], spawnPosY[i]))
            }
         }
      } else {
         spawnPosX = [2303, 1832, 1656, 1379, 1171]
         spawnPosY = [240, 150, 300, 150, 240]

         for (int i = 0; i < maxSpawn; i++) {
            int rndMob = 9400519 + Math.floor(Math.random() * 4).intValue()
            int rndPos = Math.floor(Math.random() * 5).intValue()

            MapleMonster mobObj = MapleLifeFactory.getMonster(rndMob)
            mapObj.spawnMonsterOnGroundBelow(mobObj, new Point(spawnPosX[rndPos], spawnPosY[rndPos]))
         }
      }
   }

   static def generateCombo1() {
      int[] positions = [0, 0, 0, 0, 0, 0, 0, 0, 0]
      int rndPicked = Math.floor(Math.random() * Math.pow(3, 5)).intValue()

      while (rndPicked > 0) {
         (positions[rndPicked % 3])++

         rndPicked = Math.floor(rndPicked / 3).intValue()
      }

      String returnString = ""
      for (int i = 0; i < positions.length; i++) {
         returnString += positions[i]
         if (i != positions.length - 1) {
            returnString += ","
         }
      }

      return returnString
   }

   static def generateCombo2() {
      int toPick = 5, rndPicked
      int[] positions = [0, 0, 0, 0, 0, 0, 0, 0, 0]
      while (toPick > 0) {
         rndPicked = Math.floor(Math.random() * 9).intValue()

         if (positions[rndPicked] == 0) {
            positions[rndPicked] = 1
            toPick--
         }
      }

      String returnString = ""
      for (int i = 0; i < positions.length; i++) {
         returnString += positions[i]
         if (i != positions.length - 1) {
            returnString += ","
         }
      }

      return returnString
   }

   int curMap, stage

   def clearStage(stage, eim, curMap) {
      eim.setProperty(stage + "stageclear", "true")
      if (stage > 1) {
         eim.showClearEffect(true)
         eim.linkToNextStage(stage, "apq", curMap)  //opens the portal to the next map
      } else {
         cm.getMap().getPortal("go01").setPortalState(false)

         int val = Math.floor(Math.random() * 3).intValue()
         eim.showClearEffect(670010200, "gate" + val, 2)

         cm.getMap().getPortal("go0" + val).setPortalState(true)
         eim.linkPortalToScript(stage, "go0" + val, "apq0" + val, curMap)
      }
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
            cm.sendNext("The portal is already open, advance for the trials that awaits you there.")
         } else {
            if (eim.isEventLeader(cm.getPlayer())) {
               int state = eim.getIntProperty("statusStg" + stage)

               if (state == -1) {           // preamble
                  if (stage == 1) {
                     cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k of the Amorian Challenge. In this stage, talk to #p9201047#, he will pass to you further details of the mission. After shattering the Magik Mirror down there, return the shard to #p9201047# and come here to gain access to the next stage.")
                  } else if (stage == 2) {
                     cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k of the Amorian Challenge. In this stage, let 5 of your party members climb up the platforms in such a way to try for a combination to unlock the portal to the next level. When you feel ready, talk to me and I'll let you know the situation. However, be prepared, as in the case the portal does not get unlocked after a few tries, monsters will spawn.")
                  } else if (stage == 3) {
                     cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k of the Amorian Challenge. In this stage, let 5 of your party members climb up the platforms, one on each, in such a way to try for a combination to unlock the portal to the next level. When you feel ready, talk to me and I'll let you know the situation. Take hint: upon failing, count the number of slimes appearing on the scene, that will tell how many of you had their position right.")
                  }

                  int st = (autopass) ? 2 : 0
                  eim.setProperty("statusStg" + stage, st)
               } else {       // check stage completion
                  if (state == 2) {
                     eim.setProperty("statusStg" + stage, 1)
                     clearStage(stage, eim, curMap)
                     cm.dispose()
                     return
                  }

                  MapleMap map = cm.getPlayer().getMap()
                  if (stage == 1) {
                     if (eim.getIntProperty("statusStg" + stage) == 1) {
                        clearStage(stage, eim, curMap)
                     } else {
                        cm.sendOk("Talk with #p9201047# for more info on this stage.")
                     }
                  } else if (stage == 2 || stage == 3) {
                     if (map.countMonsters() == 0) {
                        int[] objset = [0, 0, 0, 0, 0, 0, 0, 0, 0]
                        int playersOnCombo = 0
                        MapleCharacter[] party = cm.getEventInstance().getPlayers()
                        for (int i = 0; i < party.size(); i++) {
                           for (int y = 0; y < map.getAreas().size(); y++) {
                              if (map.getArea(y).contains(party[i].getPosition())) {
                                 playersOnCombo++
                                 objset[y] += 1
                                 break
                              }
                           }
                        }

                        if (playersOnCombo == 5/* || cm.getPlayer().gmLevel() > 1*/ || debug) {
                           String comboStr = eim.getProperty("stage" + stage + "combo")
                           if (comboStr == null || comboStr == "") {
                              if (stage == 2) {
                                 comboStr = generateCombo1()
                              } else {
                                 comboStr = generateCombo2()
                              }

                              eim.setProperty("stage" + stage + "combo", comboStr)
                              if (debug) {
                                 print("generated " + comboStr + " for stg" + stage + "\n")
                              }
                           }

                           String[] combo = comboStr.split(',')
                           boolean correctCombo = true
                           int guessedRight = objset.length
                           int playersRight = 0

                           if (!debug) {
                              for (int i = 0; i < objset.length; i++) {
                                 if ((combo[i]).toInteger() != objset[i]) {
                                    correctCombo = false
                                    guessedRight--
                                 } else {
                                    if (objset[i] > 0) {
                                       playersRight++
                                    }
                                 }
                              }
                           } else {
                              for (int i = 0; i < objset.length; i++) {
                                 int ci = cm.getPlayer().countItem(4000000 + i)

                                 if (ci != (combo[i]).toInteger()) {
                                    correctCombo = false
                                    guessedRight--
                                 } else {
                                    if (ci > 0) {
                                       playersRight++
                                    }
                                 }
                              }
                           }


                           if (correctCombo/* || cm.getPlayer().gmLevel() > 1*/) {
                              eim.setProperty("statusStg" + stage, 1)
                              clearStage(stage, eim, curMap)
                              cm.dispose()
                           } else {
                              int miss = eim.getIntProperty("missCount") + 1
                              int maxMiss = (stage == 2) ? 7 : 1

                              if (miss < maxMiss) {   //already implies stage 2
                                 eim.setIntProperty("missCount", miss)

                                 if (guessedRight == 6) { //6 unused slots on this stage
                                    cm.sendNext("All ropes weigh differently. Think your next course of action, then try again.")
                                    MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Amos: Hmm... All ropes weigh differently.")
                                 } else {
                                    cm.sendNext("One rope weigh the same. Think your next course of action, then try again.")
                                    MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Amos: Hmm... One rope weigh the same.")
                                 }
                              } else {
                                 spawnMobs(playersRight)
                                 eim.setIntProperty("missCount", 0)
                                 if (stage == 2) {
                                    eim.setProperty("stage2combo", "")

                                    cm.sendNext("You have failed to discover the right combination, now it shall be reset. Start over again!")
                                    MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Amos: You have failed to discover the right combination, now it shall be reset. Start over again!")
                                 }
                              }

                              eim.showWrongEffect()
                              cm.dispose()
                           }
                        } else {
                           if (stage == 2) {
                              cm.sendNext("It looks like you guys haven't found the ways of this trial yet. Think on an arrangement of 5 members on the platforms. Remember, exactly 5 are allowed to stand on the platforms, and if you move it may not count as an answer, so please keep that in mind. Keep going!")
                           } else {
                              cm.sendNext("It looks like you guys haven't found the ways of this trial yet. Think on an arrangement of party members on different platforms. Remember, exactly 5 are allowed to stand on the platforms, and if you move it may not count as an answer, so please keep that in mind. Keep going!")
                           }

                           cm.dispose()
                        }
                     } else {
                        cm.sendNext("Defeat all mobs before trying for a combination.")
                     }
                  }
               }
            } else {
               cm.sendNext("Please tell your #bParty-Leader#k to come talk to me.")
            }
         }

         cm.dispose()
      }
   }
}

NPC9201044 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201044(cm: cm))
   }
   return (NPC9201044) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }