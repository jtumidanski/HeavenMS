package map.onUserEnter


import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.Point

class Map926000000 {

   static def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(926000000)
      map.resetPQ(1)

      if (map.countMonster(9100013) == 0) {
         map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9100013), new Point(82, 200))
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