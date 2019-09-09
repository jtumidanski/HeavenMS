package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Mappepeking_effect {

   static def start(MapScriptMethods ms) {
      int mobId = 3300000 + (Math.floor(Math.random() * 3).intValue() + 5)
      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(-28, -67))
   }
}

Mappepeking_effect getMap() {
   getBinding().setVariable("map", new Mappepeking_effect())
   return (Mappepeking_effect) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}