package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.life.MapleLifeFactory
import server.life.MapleMonster
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("sMob1").getState() >= 1 && pi.getMap().getReactorByName("sMob2").getState() >= 1 && pi.getMap().getReactorByName("sMob3").getState() >= 1 && pi.getMap().getReactorByName("sMob4").getState() >= 1 && pi.getMap().getMonsters().size() == 0) {
      EventInstanceManager eim = pi.getEventInstance()

      if (eim.getProperty("spawnedBoss") == null) {
         int level = (eim.getProperty("level")).toInteger()
         int chests = (eim.getProperty("openedChests")).toInteger()

         Optional<MapleMonster> boss
         if (chests == 0) {
            //lord pirate
            boss = MapleLifeFactory.getMonster(9300119)
         } else if (chests == 1) {
            //angry lord pirate
            boss = MapleLifeFactory.getMonster(9300105)
         } else {
            //enraged lord pirate
            boss = MapleLifeFactory.getMonster(9300106)
         }

         boss.ifPresent({ monster ->
            monster.changeDifficulty(level, true)
            pi.getMap(925100500).spawnMonsterOnGroundBelow(monster, new Point(777, 140))
            eim.setProperty("spawnedBoss", "true")
         })
      }

      pi.playPortalSound(); pi.warp(925100500, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PORTAL_NOT_YET_OPENED"))
      return false
   }
}