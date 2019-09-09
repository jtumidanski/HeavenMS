package map.onUserEnter


import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap

import java.awt.Point

class Map108010501 {

   static def start(MapScriptMethods ms) {
      if(ms.getMapId() == 108010101) { // Archer
         spawnMob(188, 20, 9001002, ms.getPlayer().getMap())
      } else if(ms.getMapId() == 108010301) { // Warrior
         spawnMob(188, 20, 9001000, ms.getPlayer().getMap())
      } else if(ms.getMapId() == 108010201) { // Mage
         spawnMob(188, 20, 9001001, ms.getPlayer().getMap())
      } else if(ms.getMapId() == 108010401) { // Thief
         spawnMob(188, 20, 9001003, ms.getPlayer().getMap())
      } else if(ms.getMapId() == 108010501) { // Pirate
         spawnMob(188, 20, 9001008, ms.getPlayer().getMap())
      }
   }

   static def spawnMob(x, y, int id, MapleMap map) {
      if(map.getMonsterById(id) != null)
         return

      MapleMonster mob = MapleLifeFactory.getMonster(id)
      map.spawnMonsterOnGroundBelow(mob, new Point(x, y))
   }
}

Map108010501 getMap() {
   getBinding().setVariable("map", new Map108010501())
   return (Map108010501) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}