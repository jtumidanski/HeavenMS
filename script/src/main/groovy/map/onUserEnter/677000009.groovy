package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

import java.awt.*

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

      MapleLifeFactory.getMonster(mobId).ifPresent({ monster ->
         map.spawnMonsterOnGroundBelow(monster, pos)
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ON_USER_ENTER").with(mobName))
      })
   }
}

Map677000009 getMap() {
   getBinding().setVariable("map", new Map677000009())
   return (Map677000009) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}