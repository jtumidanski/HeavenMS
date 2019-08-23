package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Map910510000 {

   static def start(MapScriptMethods ms) {
      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (player.isCygnus()) {
         if (ms.isQuestStarted(20730) && ms.getQuestProgress(20730, 9300285) == 0) {
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300285), new Point(680, 258))
         }
      } else {
         if (ms.isQuestStarted(21731) && ms.getQuestProgress(21731, 9300344) == 0) {
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300344), new Point(680, 258))
         }
      }
   }
}

Map910510000 getMap() {
   getBinding().setVariable("map", new Map910510000())
   return (Map910510000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}