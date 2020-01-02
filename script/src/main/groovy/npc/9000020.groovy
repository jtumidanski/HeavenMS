package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000020 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] travelFrom = [777777777, 541000000]
   int[] travelFee = [3000, 10000]
   int[] travelMap = [800000000, 550000000]
   String[] travelPlace = ["Mushroom Shrine of Japan", "Trend Zone of Malaysia"]
   String[] travelPlaceShort = ["Mushroom Shrine", "Metropolis"]
   String[] travelPlaceCountry = ["Japan", "Malaysia"]
   String[] travelAgent = ["I", "#r#p9201135##k"]
   String[] travelDescription = ["If you desire to feel the essence of Japan, there's nothing like visiting the Shrine, a Japanese cultural melting pot. Mushroom Shrine is a mythical place that serves the incomparable Mushroom God from ancient times.",
                                 "If you desire to feel the heat of the tropics on an upbeat environment, the residents of Malaysia are eager to welcome you. Also, the metropolis itself is the heart of the local economy, that place is known to always offer something to do or to visit around."]
   String[] travelDescription2 = ["Check out the female shaman serving the Mushroom God, and I strongly recommend trying Takoyaki, Yakisoba, and other delicious food sold in the streets of Japan. Now, let's head over to #bMushroom Shrine#k, a mythical place if there ever was one.",
                                  "Once there, I strongly suggest you to schedule a visit to Kampung Village. Why? Surely you've come to know about the fantasy theme park Spooky World? No? It's simply put the greatest theme park around there, it's worth a visit! Now, let's head over to the #bTrend Zone of Malaysia#k."]
   int travelType
   int travelStatus

   def start() {
      travelStatus = getTravelingStatus(cm.getPlayer().getMapId())
      action((byte) 1, (byte) 0, 0)
   }

   def getTravelingStatus(int mapId) {
      for (int i = 0; i < travelMap.length; i++) {
         if (mapId == travelMap[i]) {
            return i
         }
      }

      return -1
   }

   def getTravelType(int mapId) {
      for (int i = 0; i < travelFrom.length; i++) {
         if (mapId == travelFrom[i]) {
            return i
         }
      }

      return 0
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && status == 4) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }

      if (travelStatus != -1) {
         if (status == 0) {
            cm.sendSimple("How's the traveling? Are you enjoying it?#b\r\n#L0#Yes, I'm done with traveling. Can I go back to #m" + cm.getPlayer().peekSavedLocation("WORLDTOUR") + "#?\r\n#L1#No, I'd like to continue exploring this place.")
         } else if (status == 1) {
            if (selection == 0) {
               cm.sendNext("Alright. I'll take you back to where you were before the visit to Japan. If you ever feel like traveling again down the road, please let me know!")
            } else if (selection == 1) {
               cm.sendOk("OK. If you ever change your mind, please let me know.")
               cm.dispose()
            }
         } else if (status == 2) {
            int map = cm.getPlayer().getSavedLocation("WORLDTOUR")
            if (map == -1) {
               map = 104000000
            }

            cm.warp(map)
            cm.dispose()
         }
      } else {
         if (status == 0) {
            travelType = getTravelType(cm.getPlayer().getMapId())
            cm.sendNext("If you're tired of the monotonous daily life, how about getting out for a change? there's nothing quite like soaking up a new culture, learning something new by the minute! It's time for you to get out and travel. We, at the Maple Travel Agency recommend you going on a #bWorld Tour#k! Are you worried about the travel expense? You shouldn't be! We, the #bMaple Travel Agency#k, have carefully come up with a plan to let you travel for ONLY #b" + cm.numberWithCommas(travelFee[travelType]) + " mesos#k!")
         } else if (status == 1) {
            cm.sendSimple("We currently offer this place for you traveling pleasure: #b" + travelPlace[travelType] + "#k. " + travelAgent[travelType] + "'ll be there serving you as the travel guide. Rest assured, the number of destinations will be increase over time. Now, would you like to head over to the " + travelPlaceShort[travelType] + "?#b\r\n#L0#Yes, take me to " + travelPlaceShort[travelType] + " (" + travelPlaceCountry[travelType] + ")")
         } else if (status == 2) {
            cm.sendNext("Would you like to travel to #b" + travelPlace[travelType] + "#k? " + travelDescription[travelType])
         } else if (status == 3) {
            if (cm.getMeso() < travelFee[travelType]) {
               cm.sendNext("You don't have enough mesos to take the travel.")
               cm.dispose()
               return
            }
            cm.sendNextPrev(travelDescription2[travelType])
         } else if (status == 4) {
            cm.gainMeso(-travelFee[travelType])
            cm.getPlayer().saveLocation("WORLDTOUR")
            cm.warp(travelMap[travelType], 0)
            cm.dispose()
         }
      }
   }
}

NPC9000020 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000020(cm: cm))
   }
   return (NPC9000020) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }