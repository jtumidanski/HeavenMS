package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.life.MapleLifeFactory
import server.life.MapleMonster
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("sMob1").getState() >= 1 && pi.getMap().getReactorByName("sMob2").getState() >= 1 && pi.getMap().getReactorByName("sMob3").getState() >= 1 && pi.getMap().getReactorByName("sMob4").getState() >= 1 && pi.getMap().getMonsters().size() == 0) {
      EventInstanceManager eim = pi.getEventInstance()

      if (eim.getProperty("spawnedBoss") == null) {
         int level = (eim.getProperty("level")).toInteger()
         int chests = (eim.getProperty("openedChests")).toInteger()
         MapleMonster boss

         if (chests == 0) {
            boss = MapleLifeFactory.getMonster(9300119)
         }        //lord pirate
         else if (chests == 1) {
            boss = MapleLifeFactory.getMonster(9300105)
         }   //angry lord pirate
         else {
            boss = MapleLifeFactory.getMonster(9300106)
         }                   //enraged lord pirate

         boss.changeDifficulty(level, true)

         pi.getMap(925100500).spawnMonsterOnGroundBelow(boss, new Point(777, 140))
         eim.setProperty("spawnedBoss", "true")
      }

      pi.playPortalSound(); pi.warp(925100500, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The portal is not opened yet.")
      return false
   }
}