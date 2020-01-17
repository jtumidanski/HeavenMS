package map.onUserEnter

import client.MapleCharacter
import scripting.map.MapScriptMethods
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

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

      MapleLifeFactory.getMonster(mobId).ifPresent({ monster ->
         map.spawnMonsterOnGroundBelow(monster, pos)
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ON_USER_ENTER").with(mobName))
      })
   }
}

Map677000007 getMap() {
   getBinding().setVariable("map", new Map677000007())
   return (Map677000007) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}