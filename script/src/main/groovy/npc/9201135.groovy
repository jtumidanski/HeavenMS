package npc
import tools.I18nMessage

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201135 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] inMap = [540000000, 550000000, 551000000]
   List toMap = [550000000, [551000000, 541000000], 550000000]
   List cost = [42000, [10000, 0], 10000]
   List toMapSp = [0, [2, 4], 4]

   int location
   String text

   int travelCost
   int travelMap
   int travelSp

   boolean startedTravel = false

   def start() {
      if (cm.getPlayer().getMap().getId() != 540000000) {
         text = "Hey I'm #p9201135#, your tour guide here in #rMalaysia#k. Where would you like to travel?\n\n"
      } else {
         text = "Hey I'm #p9201135#, a tour guide on #rMalaysia#k. Since you're not registered in our special travel package with our partner #bMaple Travel Agency#k, the ride will be significantly more expensive. So, would you like to ride now?\n\n"
         startedTravel = true
      }

      for (int i = 0; i < toMap.size(); i++) {
         if (inMap[i] == cm.getPlayer().getMap().getId()) {
            if (inMap[i] == 550000000 && toMap[1] instanceof ArrayList) {
               List temp = (List) toMap[1]
               temp[1] = cm.getPlayer().peekSavedLocation("WORLDTOUR")
               if (temp[1] == -1) {
                  temp[1] = 541000000
               }
            }

            location = i
            break
         }
      }

      if (toMap[location] instanceof ArrayList && cost[location] instanceof ArrayList) {
         List maps = (List) toMap[location]
         List costs = (List) cost[location]

         for (int i = 0; i < maps.size(); i++) {
            text += "\t\r\n#b#L"
            text += i + "##m"
            text += maps[i]
            text += "# "
            text += (costs[i] > 0 ? "(" + costs[i] + "mesos)" : "")
            text += "#l"
         }
      } else {
         text += "\t\r\n#b#L0##m"
         text += toMap[location]
         text += "# "
         text += (cost[location] > 0 ? "(" + cost[location] + "mesos)" : "")
         text += "#l"
      }

      text += "#k"

      cm.sendSimple(text)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.sendNext(I18nMessage.from("9201135_KNOW_WHERE_TO_COME"))
         cm.dispose()
         return
      } else {
         status++
      }
      if (status == 1) {
         if (toMap[location] == null) {
            cm.dispose()
            return
         }

         if (toMap[location] instanceof ArrayList && cost[location] instanceof ArrayList && toMapSp[location] instanceof ArrayList) {
            List maps = (List) toMap[location]
            List costs = (List) cost[location]
            List sps = (List) toMapSp[location]

            travelCost = costs[selection] as Integer
            travelMap = maps[selection] as Integer
            travelSp = sps[selection] as Integer
         } else {
            travelCost = (int) cost[location]
            travelMap = (int) toMap[location]
            travelSp = (int) toMapSp[location]
         }

         if (travelCost > 0) {
            cm.sendYesNo(I18nMessage.from("9201135_WOULD_YOU_LIKE_TO_TRAVEL").with(travelMap, travelMap, cm.numberWithCommas(travelCost)))
         } else {
            cm.sendNext(I18nMessage.from("9201135_HAD_A_GREAT_TIME"))
         }
      } else if (status == 2) {
         if (cm.getMeso() < travelCost) {
            cm.sendNext(I18nMessage.from("9201135_NOT_ENOUGH_MESO"))
         } else {
            if (travelCost > 0) {
               cm.gainMeso(-travelCost)
               if (startedTravel) {
                  cm.getPlayer().saveLocation("WORLDTOUR")
               }
            } else {
               travelMap = cm.getPlayer().getSavedLocation("WORLDTOUR")
               if (travelMap == -1 && toMap[1] instanceof ArrayList) {
                  List temp = (List) toMap[1]
                  travelMap = temp[1] as Integer
               }
            }

            cm.warp(travelMap, travelSp)
         }
         cm.dispose()
      }
   }
}

NPC9201135 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201135(cm: cm))
   }
   return (NPC9201135) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }