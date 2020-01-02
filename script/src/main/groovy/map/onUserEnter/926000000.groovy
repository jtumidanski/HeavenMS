package map.onUserEnter

import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Map926000000 {

   static def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(926000000)
      map.resetPQ(1)

      if (map.countMonster(9100013) == 0) {
         MapleLifeFactory.getMonster(9100013).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(82, 200)) })
      }

      return (true)
   }
}

Map926000000 getMap() {
   getBinding().setVariable("map", new Map926000000())
   return (Map926000000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}