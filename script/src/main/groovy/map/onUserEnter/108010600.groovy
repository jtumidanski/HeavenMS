package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010600 {
   int npcId = 1104100
   Point spawnPos = new Point(2830, 78)

   def start(MapScriptMethods ms) {
      MapleMap map = ms.getMap()
      if (!map.containsNPC(npcId)) {
         ms.spawnNpc(npcId, spawnPos, map)
      }
   }
}

Map108010600 getMap() {
   getBinding().setVariable("map", new Map108010600())
   return (Map108010600) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}