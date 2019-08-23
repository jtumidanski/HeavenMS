package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010600 {
   int npcid = 1104100
   Point spawnPos = new Point(2830, 78)

   def start(MapScriptMethods ms) {
      MapleMap mapobj = ms.getMap()

      if (!mapobj.containsNPC(npcid)) {
         ms.spawnNpc(npcid, spawnPos, mapobj)
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