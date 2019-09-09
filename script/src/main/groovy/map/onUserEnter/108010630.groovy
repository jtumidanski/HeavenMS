package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010630 {
   int npcid = 1104103
   Point spawnPos = new Point(-2263, -582)

   def start(MapScriptMethods ms) {
      MapleMap mapobj = ms.getMap()

      if (!mapobj.containsNPC(npcid)) {
         ms.spawnNpc(npcid, spawnPos, mapobj)
      }
   }
}

Map108010630 getMap() {
   getBinding().setVariable("map", new Map108010630())
   return (Map108010630) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}