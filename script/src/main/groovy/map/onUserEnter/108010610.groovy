package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010610 {
   int npcId = 1104101
   Point spawnPos = new Point(3395, -322)

   def start(MapScriptMethods ms) {
      MapleMap map = ms.getMap()
      if (!map.containsNPC(npcId)) {
         ms.spawnNpc(npcId, spawnPos, map)
      }
   }
}

Map108010610 getMap() {
   getBinding().setVariable("map", new Map108010610())
   return (Map108010610) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}