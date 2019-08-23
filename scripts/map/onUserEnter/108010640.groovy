package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010640 {
   int npcid = 1104104
   Point spawnPos = new Point(372, 70)

   def start(MapScriptMethods ms) {
      MapleMap mapobj = ms.getMap()

      if (!mapobj.containsNPC(npcid)) {
         ms.spawnNpc(npcid, spawnPos, mapobj)
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