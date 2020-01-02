package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class MapPepeKingEffect {

   static def start(MapScriptMethods ms) {
      int mobId = 3300000 + (Math.floor(Math.random() * 3).intValue() + 5)
      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      MapleLifeFactory.getMonster(mobId).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-28, -67)) })
   }
}

MapPepeKingEffect getMap() {
   getBinding().setVariable("map", new MapPepeKingEffect())
   return (MapPepeKingEffect) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}