package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

class Map677000007 {

   static def start(MapScriptMethods ms) {
      Point pos = new Point(171, 50)
      int mobId = 9400611
      String mobName = "Crocell"

      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (map.getMonsterById(mobId) != null) {
         return
      }

      map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), pos)
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, mobName + " has appeared!")
   }
}

Map677000007 getMap() {
   getBinding().setVariable("map", new Map677000007())
   return (Map677000007) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}