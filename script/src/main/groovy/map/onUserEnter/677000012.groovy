package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

class Map677000012 {

   static def start(MapScriptMethods ms) {
      Point pos = new Point(842, 0)
      int mobId = 9400633
      String mobName = "Astaroth"

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

Map677000012 getMap() {
   getBinding().setVariable("map", new Map677000012())
   return (Map677000012) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}