package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.Point

class Map677000009 {

   static def start(MapScriptMethods ms) {
      Point pos = new Point(251, -841)
      int mobId = 9400613
      String mobName = "Valefor"

      MapleCharacter player = ms.getPlayer()
      MapleMap map = player.getMap()

      if (map.getMonsterById(mobId) != null) {
         return
      }

      map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), pos)
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, mobName + " has appeared!")
   }
}

Map677000009 getMap() {
   getBinding().setVariable("map", new Map677000009())
   return (Map677000009) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}