package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Map925040100 {

   static def start(MapScriptMethods ms) {
      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (ms.isQuestStarted(21747) && ms.getQuestProgressInt(21747, 9300351) == 0) {
         map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300351), new Point(897, 51))
      }
   }
}

Map925040100 getMap() {
   getBinding().setVariable("map", new Map925040100())
   return (Map925040100) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}