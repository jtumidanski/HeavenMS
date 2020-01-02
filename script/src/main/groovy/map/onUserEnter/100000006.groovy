package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Map100000006 {

   static def start(MapScriptMethods ms) {
      if (ms.getQuestStatus(2175) == 1) {
         int mobId = 9300156
         MapleCharacter player = ms.getPlayer()
         MapleMap map = player.getMap()

         if (map.getMonsterById(mobId) != null) {
            return
         }

         MapleLifeFactory.getMonster(mobId).ifPresent({ mob -> map.spawnMonsterOnGroundBelow(mob, new Point(-1027, 216)) })
      }
   }
}

Map100000006 getMap() {
   getBinding().setVariable("map", new Map100000006())
   return (Map100000006) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}