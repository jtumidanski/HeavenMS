package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

class Map677000001 {

   static def start(MapScriptMethods ms) {
      Point pos = new Point(461, 61)
      int mobId = 9400612
      String mobName = "Marbas"

      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (map.getMonsterById(mobId) != null) {
         return
      }

      map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), pos)
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, mobName + " has appeared!")
   }
}

Map677000001 getMap() {
   getBinding().setVariable("map", new Map677000001())
   return (Map677000001) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}