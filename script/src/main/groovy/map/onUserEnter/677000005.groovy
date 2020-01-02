package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

class Map677000005 {

   static def start(MapScriptMethods ms) {
      Point pos = new Point(201, 80)
      int mobId = 9400609
      String mobName = "Andras"

      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (map.getMonsterById(mobId) != null) {
         return
      }

      MapleLifeFactory.getMonster(mobId).ifPresent({ monster ->
         map.spawnMonsterOnGroundBelow(monster, pos)
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, mobName + " has appeared!")
      })
   }
}

Map677000005 getMap() {
   getBinding().setVariable("map", new Map677000005())
   return (Map677000005) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}