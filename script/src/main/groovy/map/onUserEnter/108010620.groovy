package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010620 {
   int npcId = 1104102
   Point spawnPos = new Point(500, -522)

   def start(MapScriptMethods ms) {
      MapleMap map = ms.getMap()
      if (!map.containsNPC(npcId)) {
         ms.spawnNpc(npcId, spawnPos, map)
      }
   }
}

Map108010620 getMap() {
   getBinding().setVariable("map", new Map108010620())
   return (Map108010620) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}