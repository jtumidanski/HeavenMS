package npc
import tools.I18nMessage

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
            if (areaRects[j].contains(players[i].position())) {
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
                  cm.sendNext(I18nMessage.from("9020001_PLEASE_HURRY"))
                  cm.dispose()
               } else {
                  cm.sendNext(I18nMessage.from("9020001_INCREDIBLE"))
               }
            } else if (curMap == 103000800) {   // stage 1
               if (cm.isEventLeader()) {
                  int numberOfPasses = eim.getPlayerCount() - 1     // minus leader

                  if (cm.hasItem(4001008, numberOfPasses)) {
                     cm.sendNext(I18nMessage.from("9020001_CONGRATULATIONS").with(numberOfPasses))
                     clearStage(stage, eim, curMap)
                     eim.gridClear()
                     cm.gainItem(4001008, (short) -numberOfPasses)
                  } else {
                     cm.sendNext(I18nMessage.from("9020001_SHORT_PASSES").with(numberOfPasses))
                  }
               } else {
                  int data = eim.gridCheck(cm.getPlayer())

                  if (data == 0) {
                     cm.sendNext(I18nMessage.from("9020001_THANK_YOU"))
                  } else if (data == -1) {
                     data = Math.floor(Math.random() * stage1Questions.length).intValue() + 1
                     //data will be counted from 1
                     eim.gridInsert(cm.getPlayer(), data)

                     String question = stage1Questions[data - 1]
                     cm.sendNext(question)
                  } else {
                     int answer = stage1Answers[data - 1]

                     if (cm.itemQuantity(4001007) == answer) {
                        cm.sendNext(I18nMessage.from("9020001_RIGHT_ANSWER"))
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

               String nthText = "2nd", nthObject = "ropes", nthVerb = "hang", nthPosition = "hang on the ropes too low"

               if (!eim.isEventLeader(cm.getPlayer())) {
                  cm.sendOk(I18nMessage.from("9020001_FOLLOW_INSTRUCTIONS"))
               } else if (eim.getProperty(stgProperty) == null) {
                  cm.sendNext(I18nMessage.from("9020001_WELCOME").with(nthText, nthObject, nthObject, nthObject, nthVerb, nthPosition, nthObject, nthObject, nthVerb, nthObject, nthVerb))
                  int c = Math.floor(Math.random() * stgCombos.length).intValue()
                  eim.setProperty(stgProperty, c.toString())
               } else {
                  boolean accept = rectangleStages(eim, stgProperty, stgCombos, stgAreas)

                  if (accept) {
                     clearStage(stage, eim, curMap)
                     cm.sendNext(I18nMessage.from("9020001_PLEASE_HURRY"))
                  } else {
                     eim.showWrongEffect()
                     cm.sendNext(I18nMessage.from("9020001_YOU_HAVE_NOT_FOUND").with(nthObject, nthObject, nthVerb, nthObject, nthPosition))
                  }
               }

               cm.dispose()
            } else if (curMap == 103000802) {
               String stgProperty = "stg3Property"
               int[][] stgCombos = stage3Combos
               Rectangle[] stgAreas = stage3Rects

               String nthText = "3rd", nthObject = "platforms", nthVerb = "stand", nthPosition = "stand too close to the edges"

               if (!eim.isEventLeader(cm.getPlayer())) {
                  cm.sendOk(I18nMessage.from("9020001_FOLLOW_INSTRUCTIONS"))
               } else if (eim.getProperty(stgProperty) == null) {
                  cm.sendNext(I18nMessage.from("9020001_WELCOME").with(nthText, nthObject, nthObject, nthObject, nthVerb, nthPosition, nthObject, nthObject, nthVerb, nthObject, nthVerb))
                  int c = Math.floor(Math.random() * stgCombos.length).toInteger()
                  eim.setProperty(stgProperty, c.toString())
               } else {
                  boolean accept = rectangleStages(eim, stgProperty, stgCombos, stgAreas)

                  if (accept) {
                     clearStage(stage, eim, curMap)
                     cm.sendNext(I18nMessage.from("9020001_PLEASE_HURRY"))
                  } else {
                     eim.showWrongEffect()
                     cm.sendNext(I18nMessage.from("9020001_YOU_HAVE_NOT_FOUND").with(nthObject, nthObject, nthVerb, nthObject, nthPosition))
                  }
               }

               cm.dispose()
            } else if (curMap == 103000803) {
               String stgProperty = "stg4Property"
               int[][] stgCombos = stage4Combos
               Rectangle[] stgAreas = stage4Rects

               String nthText = "4th", nthObject = "barrels", nthVerb = "stand", nthPosition = "stand too close to the edges"

               if (!eim.isEventLeader(cm.getPlayer())) {
                  cm.sendOk(I18nMessage.from("9020001_FOLLOW_INSTRUCTIONS"))
               } else if (eim.getProperty(stgProperty) == null) {
                  cm.sendNext(I18nMessage.from("9020001_WELCOME").with(nthText, nthObject, nthObject, nthObject, nthVerb, nthPosition, nthObject, nthObject, nthVerb, nthObject, nthVerb))
                  int c = Math.floor(Math.random() * stgCombos.length).toInteger()
                  eim.setProperty(stgProperty, c.toString())
               } else {
                  boolean accept = rectangleStages(eim, stgProperty, stgCombos, stgAreas)

                  if (accept) {
                     clearStage(stage, eim, curMap)
                     cm.sendNext(I18nMessage.from("9020001_PLEASE_HURRY"))
                  } else {
                     eim.showWrongEffect()
                     cm.sendNext(I18nMessage.from("9020001_YOU_HAVE_NOT_FOUND").with(nthObject, nthObject, nthVerb, nthObject, nthPosition))
                  }
               }

               cm.dispose()
            } else if (curMap == 103000804) {
               if (eim.isEventLeader(cm.getPlayer())) {
                  if (cm.haveItem(4001008, 10)) {
                     cm.sendNext(I18nMessage.from("9020001_LAST_BONUS_STAGE"))
                     cm.gainItem(4001008, (short) -10)

                     clearStage(stage, eim, curMap)
                     eim.clearPQ()
                  } else {
                     cm.sendNext(I18nMessage.from("9020001_FINAL_STAGE"))
                  }
               } else {
                  cm.sendNext(I18nMessage.from("9020001_WELCOME_FINAL_STAGE"))
               }

               cm.dispose()
            }
         } else if (status == 1) {
            if (!eim.giveEventReward(cm.getPlayer())) {
               cm.sendNext(I18nMessage.from("9020001_MAKE_INVENTORY_ROOM"))
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