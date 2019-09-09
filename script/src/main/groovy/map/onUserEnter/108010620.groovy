package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap

import java.awt.*

class Map108010620 {
   int npcid = 1104102
   Point spawnPos = new Point(500, -522)

   def start(MapScriptMethods ms) {
      MapleMap mapobj = ms.getMap()

      if (!mapobj.containsNPC(npcid)) {
         ms.spawnNpc(npcid, spawnPos, mapobj)
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