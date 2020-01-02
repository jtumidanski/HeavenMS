package npc

import client.MapleCharacter
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleMap

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040043 {
   NPCConversationManager cm

   boolean debug = false
   int status = 0
   int curMap, stage
   int sel = -1
   int[] boxSet

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 922010100) / 100).intValue() + 1
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def generateCombo() {
      int countPicked = 0
      int[] positions = [0, 0, 0, 0, 0, 0, 0, 0, 0]
      while (countPicked < 5) {
         int picked = Math.floor(Math.random() * positions.length).intValue()
         if (positions[picked] == 1) // Don't let it pick one its already picked.
         {
            continue
         }

         positions[picked] = 1
         countPicked++
      }

      String returnString = ""
      for (int i = 0; i < positions.length; i++ ) {
         returnString += positions[i]
         if (i != positions.length - 1) {
            returnString += ","
         }
      }

      return returnString
   }

   static def clearStage(int stage, EventInstanceManager eim, int curMap) {
      eim.setProperty(stage + "stageclear", "true")
      eim.showClearEffect(true)

      eim.linkToNextStage(stage, "lpq", curMap)  //opens the portal to the next map
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
            cm.sendNext("Hurry, goto the next stage, the portal is open!")
         } else {
            if (eim.isEventLeader(cm.getPlayer())) {
               int state = eim.getIntProperty("statusStg" + stage)

               if (state == -1) {           // preamble
                  cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k. In this stage, line up 5 member of your party above those boxes in order to form the right combination to unlock the next stage. Only one player should stay above a box desired to pertain the combination.")

                  int st = (debug) ? 2 : 0
                  eim.setProperty("statusStg" + stage, st)
               } else {       // check stage completion
                  if (state == 2) {
                     eim.setProperty("statusStg" + stage, 1)
                     clearStage(stage, eim, curMap)
                     cm.dispose()
                     return
                  }

                  boxSet = [0, 0, 0, 0, 0, 0, 0, 0, 0]
                  int playersOnCombo = 0
                  MapleMap map = cm.getPlayer().getMap()
                  MapleCharacter[] party = cm.getEventInstance().getPlayers()
                  for (int i = 0; i < party.size(); i++ ) {
                     for (int y = 0; y < map.getAreas().size(); y++ ) {
                        if (map.getArea(y).contains(party[i].position())) {
                           playersOnCombo++
                           boxSet[y] = 1
                           //cm.mapMessage(5, "Player found on " + (y + 1));
                           break
                        }
                     }
                  }

                  if (playersOnCombo == 5 || cm.getPlayer().gmLevel() > 1) {
                     String comboStr = eim.getProperty("stage" + stage + "combo")
                     if (comboStr == null) {
                        comboStr = generateCombo()
                        eim.setProperty("stage" + stage + "combo", comboStr)
                     }

                     String[] combo = comboStr.split(',')
                     boolean correctCombo = true
                     for (int i = 0; i < boxSet.length && correctCombo; i++) {
                        if ((combo[i]).toInteger() != boxSet[i]) {
                           //cm.mapMessage(5, "Combo failed on " + (i + 1));
                           correctCombo = false
                        }
                     }
                     if (correctCombo || cm.getPlayer().gmLevel() > 1) {
                        eim.setProperty("statusStg" + stage, 1)
                        clearStage(stage, eim, curMap)
                        cm.dispose()
                     } else {
                        eim.showWrongEffect()
                        cm.dispose()
                     }
                  } else {
                     cm.sendNext("It looks like you haven't found the 5 boxes just yet. Please think of a different combination of boxes. Only 5 are allowed to stand on boxes, and if you move it may not count as an answer, so please keep that in mind. Keep going!")
                     cm.dispose()
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

NPC2040043 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040043(cm: cm))
   }
   return (NPC2040043) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }