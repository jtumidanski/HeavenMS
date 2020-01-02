package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010640 {
   int npcId = 1104104
   Point spawnPos = new Point(372, 70)

   def start(MapScriptMethods ms) {
      MapleMap map = ms.getMap()
      if (!map.containsNPC(npcId)) {
         ms.spawnNpc(npcId, spawnPos, map)
      }
   }
}

Map108010640 getMap() {
   getBinding().setVariable("map", new Map108010640())
   return (Map108010640) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}