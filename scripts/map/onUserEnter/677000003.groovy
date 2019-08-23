package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Map677000003 {

   static def start(MapScriptMethods ms) {
      Point pos = new Point(467, 0)
      int mobId = 9400610
      String mobName = "Amdusias"

      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (map.getMonsterById(mobId) != null) {
         return
      }

      map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), pos)
      player.message(mobName + " has appeared!")
   }
}

Map677000003 getMap() {
   getBinding().setVariable("map", new Map677000003())
   return (Map677000003) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}