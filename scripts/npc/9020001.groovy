package npc

import client.MapleCharacter
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9020001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventInstanceManager eim

   String[] stage1Questions = [
         "Here's the question. Collect the same number of coupons as the minimum level required to make the first job advancement as warrior.",
         "Here's the question. Collect the same number of coupons as the minimum amount of STR needed to make the first job advancement as a warrior.",
         "Here's the question. Collect the same number of coupons as the minimum amount of INT needed to make the first job advancement as a magician.",
         "Here's the question. Collect the same number of coupons as the minimum amount of DEX needed to make the first job advancement as a bowman.",
         "Here's the question. Collect the same number of coupons as the minimum amount of DEX needed to make the first job advancement as a thief.",
         "Here's the question. Collect the same number of coupons as the minimum level required to advance to 2nd job.",
         "Here's the question. Collect the same number of coupons as the minimum level required to make the first job advancement as a magician."]
   int[] stage1Answers = [10, 35, 20, 25, 25, 30, 8]

   Rectangle[] stage2Rects = [new Rectangle(-755, -132, 4, 218), new Rectangle(-721, -340, 4, 166),
                              new Rectangle(-586, -326, 4, 150), new Rectangle(-483, -181, 4, 222)]
   Rectangle[] stage3Rects = [new Rectangle(608, -180, 140, 50), new Rectangle(791, -117, 140, 45),
                              new Rectangle(958, -180, 140, 50), new Rectangle(876, -238, 140, 45),
                              new Rectangle(702, -238, 140, 45)]
   Rectangle[] stage4Rects = [new Rectangle(910, -236, 35, 5), new Rectangle(877, -184, 35, 5),
                              new Rectangle(946, -184, 35, 5), new Rectangle(845, -132, 35, 5),
                              new Rectangle(910, -132, 35, 5), new Rectangle(981, -132, 35, 5)]

   int[][] stage2Combos = [[0, 1, 1, 1], [1, 0, 1, 1], [1, 1, 0, 1], [1, 1, 1, 0]]
   int[][] stage3Combos = [[0, 0, 1, 1, 1], [0, 1, 0, 1, 1], [0, 1, 1, 0, 1],
                           [0, 1, 1, 1, 0], [1, 0, 0, 1, 1], [1, 0, 1, 0, 1],
                           [1, 0, 1, 1, 0], [1, 1, 0, 0, 1], [1, 1, 0, 1, 0],
                           [1, 1, 1, 0, 0]]
   int[][] stage4Combos = [[0, 0, 0, 1, 1, 1], [0, 0, 1, 0, 1, 1], [0, 0, 1, 1, 0, 1],
                           [0, 0, 1, 1, 1, 0], [0, 1, 0, 0, 1, 1], [0, 1, 0, 1, 0, 1],
                           [0, 1, 0, 1, 1, 0], [0, 1, 1, 0, 0, 1], [0, 1, 1, 0, 1, 0],
                           [0, 1, 1, 1, 0, 0], [1, 0, 0, 0, 1, 1], [1, 0, 0, 1, 0, 1],
                           [1, 0, 0, 1, 1, 0], [1, 0, 1, 0, 0, 1], [1, 0, 1, 0, 1, 0],
                           [1, 0, 1, 1, 0, 0], [1, 1, 0, 0, 0, 1], [1, 1, 0, 0, 1, 0],
                           [1, 1, 0, 1, 0, 0], [1, 1, 1, 0, 0, 0]]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }


   static def clearStage(int stage, EventInstanceManager eim, int curMap) {
      eim.setProperty(stage + "stageclear", "true")
      eim.showClearEffect(true)

      eim.linkToNextStage(stage, "kpq", curMap)  //opens the portal to the next map
   }

   static def rectangleStages(EventInstanceManager eim, String property, int[][] areaCombos, Rectangle[] areaRects) {
      String c = eim.getProperty(property)
      if (c == null) {
         c = Math.floor(Math.random() * areaCombos.length)
         eim.setProperty(property, c.toString())
      } else {
         c = (c).toInteger()
      }

      // get player placement
      MapleCharacter[] players = eim.getPlayers()
      int[] playerPlacement = [0, 0, 0, 0, 0, 0]

      for (int i = 0; i < eim.getPlayerCount(); i++) {
         for (int j = 0; j < areaRects.length; j++) {
            if (areaRects[j].contains(players[i].getPosition())) {
               playerPlacement[j] += 1
               break
            }
         }
      }

      int[] curCombo = (int[]) areaCombos[c]
      boolean accept = true
      for (int j = 0; j < curCombo.length; j++) {
         if (curCombo[j] != playerPlacement[j]) {
            accept = false
            break
         }
      }

      return accept
   }

   def action(Byte mode, Byte type, Integer selection) {
      eim = cm.getEventInstance()

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
            int curMap = cm.getMapId()
            int stage = curMap - 103000800 + 1
            if (eim.getProperty(stage.toString() + "stageclear") != null) {
               if (stage < 5) {
                  cm.sendNext("Please hurry on to the next stage, the portal opened!")
                  cm.dispose()
               } else {
                  cm.sendNext("Incredible! You cleared all the stages to get to this point. Here's a small prize for your job well done. Before you accept it, however, please make sure your use and etc. inventories have empty slots available.")
               }
            } else if (curMap == 103000800) {   // stage 1
               if (cm.isEventLeader()) {
                  int numpasses = eim.getPlayerCount() - 1     // minus leader

                  if (cm.hasItem(4001008, numpasses)) {
                     cm.sendNext("You gathered up " + numpasses + " passes! Congratulations on clearing the stage! I'll make the portal that sends you to the next stage. There's a time limit on getting there, so please hurry. Best of luck to you all!")
                     clearStage(stage, eim, curMap)
                     eim.gridClear()
                     cm.gainItem(4001008, (short) -numpasses)
                  } else {
                     cm.sendNext("I'm sorry, but you are short on the number of passes. You need to give me the right number of passes; it should be the number of members of your party minus the leader, in this case the total of " + numpasses + " to clear the stage. Tell your party members to solve the questions, gather up the passes, and give them to you.")
                  }
               } else {
                  int data = eim.gridCheck(cm.getPlayer())

                  if (data == 0) {
                     cm.sendNext("Thanks for bringing me the coupons. Please hand the pass to your party leader to continue.")
                  } else if (data == -1) {
                     data = Math.floor(Math.random() * stage1Questions.length).intValue() + 1
                     //data will be counted from 1
                     eim.gridInsert(cm.getPlayer(), data)

                     String question = stage1Questions[data - 1]
                     cm.sendNext(question)
                  } else {
                     int answer = stage1Answers[data - 1]

                     if (cm.itemQuantity(4001007) == answer) {
                        cm.sendNext("That's the right answer! For that you have just received a #bpass#k. Please hand it to the leader of the party.")
                        cm.gainItem(4001007, (short) -answer)
                        cm.gainItem(4001008, (short) 1)
                        eim.gridInsert(cm.getPlayer(), 0)
                     } else {
                        String question = stage1Questions[eim.gridCheck(cm.getPlayer()) - 1]
                        cm.sendNext("I'm sorry, but that is not the right answer!\r\n" + question)
                     }
                  }
               }

               cm.dispose()
            } else if (curMap == 103000801) {   // stage 2
               String stgProperty = "stg2Property"
               int[][] stgCombos = stage2Combos
               Rectangle[] stgAreas = stage2Rects

               String nthtext = "2nd", nthobj = "ropes", nthverb = "hang", nthpos = "hang on the ropes too low"

               if (!eim.isEventLeader(cm.getPlayer())) {
                  cm.sendOk("Follow the instructions given by your party leader to proceed through this stage.")
               } else if (eim.getProperty(stgProperty) == null) {
                  cm.sendNext("Hi. Welcome to the " + nthtext + " stage. Next to me, you'll see a number of " + nthobj + ". Out of these " + nthobj + ", #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct " + nthobj + " and " + nthverb + " on them.#k\r\nBUT, it doesn't count as an answer if you " + nthpos + "; please be near the middle of the " + nthobj + " to be counted as a correct answer. Also, only 3 members of your party are allowed on the " + nthobj + ". Once they are " + nthverb + "ing on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right " + nthobj + " to " + nthverb + " on!")
                  int c = Math.floor(Math.random() * stgCombos.length).intValue()
                  eim.setProperty(stgProperty, c.toString())
               } else {
                  boolean accept = rectangleStages(eim, stgProperty, stgCombos, stgAreas)

                  if (accept) {
                     clearStage(stage, eim, curMap)
                     cm.sendNext("Please hurry on to the next stage, the portal opened!")
                  } else {
                     eim.showWrongEffect()
                     cm.sendNext("It looks like you haven't found the 3 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". Only 3 are allowed to " + nthverb + " on " + nthobj + ", and if you " + nthpos + " it may not count as an answer, so please keep that in mind. Keep going!")
                  }
               }

               cm.dispose()
            } else if (curMap == 103000802) {
               String stgProperty = "stg3Property"
               int[][] stgCombos = stage3Combos
               Rectangle[] stgAreas = stage3Rects

               String nthtext = "3rd", nthobj = "platforms", nthverb = "stand", nthpos = "stand too close to the edges"

               if (!eim.isEventLeader(cm.getPlayer())) {
                  cm.sendOk("Follow the instructions given by your party leader to proceed through this stage.")
               } else if (eim.getProperty(stgProperty) == null) {
                  cm.sendNext("Hi. Welcome to the " + nthtext + " stage. Next to me, you'll see a number of " + nthobj + ". Out of these " + nthobj + ", #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct " + nthobj + " and " + nthverb + " on them.#k\r\nBUT, it doesn't count as an answer if you " + nthpos + "; please be near the middle of the " + nthobj + " to be counted as a correct answer. Also, only 3 members of your party are allowed on the " + nthobj + ". Once they are " + nthverb + "ing on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right " + nthobj + " to " + nthverb + " on!")
                  int c = Math.floor(Math.random() * stgCombos.length).toInteger()
                  eim.setProperty(stgProperty, c.toString())
               } else {
                  boolean accept = rectangleStages(eim, stgProperty, stgCombos, stgAreas)

                  if (accept) {
                     clearStage(stage, eim, curMap)
                     cm.sendNext("Please hurry on to the next stage, the portal opened!")
                  } else {
                     eim.showWrongEffect()
                     cm.sendNext("It looks like you haven't found the 3 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". Only 3 are allowed to " + nthverb + " on " + nthobj + ", and if you " + nthpos + " it may not count as an answer, so please keep that in mind. Keep going!")
                  }
               }

               cm.dispose()
            } else if (curMap == 103000803) {
               String stgProperty = "stg4Property"
               int[][] stgCombos = stage4Combos
               Rectangle[] stgAreas = stage4Rects

               String nthtext = "4th", nthobj = "barrels", nthverb = "stand", nthpos = "stand too close to the edges"

               if (!eim.isEventLeader(cm.getPlayer())) {
                  cm.sendOk("Follow the instructions given by your party leader to proceed through this stage.")
               } else if (eim.getProperty(stgProperty) == null) {
                  cm.sendNext("Hi. Welcome to the " + nthtext + " stage. Next to me, you'll see a number of " + nthobj + ". Out of these " + nthobj + ", #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct " + nthobj + " and " + nthverb + " on them.#k\r\nBUT, it doesn't count as an answer if you " + nthpos + "; please be near the middle of the " + nthobj + " to be counted as a correct answer. Also, only 3 members of your party are allowed on the " + nthobj + ". Once they are " + nthverb + "ing on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right " + nthobj + " to " + nthverb + " on!")
                  int c = Math.floor(Math.random() * stgCombos.length).toInteger()
                  eim.setProperty(stgProperty, c.toString())
               } else {
                  boolean accept = rectangleStages(eim, stgProperty, stgCombos, stgAreas)

                  if (accept) {
                     clearStage(stage, eim, curMap)
                     cm.sendNext("Please hurry on to the next stage, the portal opened!")
                  } else {
                     eim.showWrongEffect()
                     cm.sendNext("It looks like you haven't found the 3 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". Only 3 are allowed to " + nthverb + " on " + nthobj + ", and if you " + nthpos + " it may not count as an answer, so please keep that in mind. Keep going!")
                  }
               }

               cm.dispose()
            } else if (curMap == 103000804) {
               if (eim.isEventLeader(cm.getPlayer())) {
                  if (cm.haveItem(4001008, 10)) {
                     cm.sendNext("Here's the portal that leads you to the last, bonus stage. It's a stage that allows you to defeat regular monsters a little easier. You'll be given a set amount of time to hunt as much as possible, but you can always leave the stage in the middle of it through the NPC. Again, congratulations on clearing all the stages. Let your party talk to me to receive their prizes as they are allowed to pass to the bonus stage. Take care...")
                     cm.gainItem(4001008, (short) -10)

                     clearStage(stage, eim, curMap)
                     eim.clearPQ()
                  } else {
                     cm.sendNext("Hello. Welcome to the 5th and final stage. Walk around the map and you'll be able to find some Boss monsters. Defeat all of them, gather up #bthe passes#k, and please get them to me. Once you earn your pass, the leader of your party will collect them, and then get them to me once the #bpasses#k are gathered up. The monsters may be familiar to you, but they may be much stronger than you think, so please be careful. Good luck!")
                  }
               } else {
                  cm.sendNext("Welcome to the 5th and final stage.  Walk around the map and you will be able to find some Boss monsters.  Defeat them all, gather up the #bpasses#k, and #bgive them to your leader#k.  Once you are done, return to me to collect your reward.")
               }

               cm.dispose()
            }
         } else if (status == 1) {
            if (!eim.giveEventReward(cm.getPlayer())) {
               cm.sendNext("Please make room on your inventory first!")
            } else {
               cm.warp(103000805, "st00")
            }

            cm.dispose()
         }
      }
   }
}

NPC9020001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9020001(cm: cm))
   }
   return (NPC9020001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }