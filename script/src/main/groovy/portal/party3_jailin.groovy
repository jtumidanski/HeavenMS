package portal

import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.I18nMessage
import tools.MasterBroadcaster
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.packet.field.effect.PlaySound
import tools.packet.field.effect.ShowEffect

def enterLeverSequence(PortalPlayerInteraction pi) {
   MapleMap map = pi.getMap()

   int jailn = (pi.getMap().getId() / 10) % 10
   int maxToggles = (jailn == 1) ? 7 : 6

   String mapProp = pi.getEventInstance().getProperty("jail" + jailn)

   if (mapProp == null) {
      int seq = 0

      for (int i = 1; i <= maxToggles; i++) {
         if (Math.random() < 0.5) {
            seq += (1 << i)
         }
      }

      pi.getEventInstance().setProperty("jail" + jailn, seq)
      mapProp = seq
   }

   int mapProp2 = (mapProp).toInteger()
   if (mapProp2 != 0) {
      int countMiss = 0
      for (int i = 1; i <= maxToggles; i++) {
         if (!(pi.getMap().getReactorByName("lever" + i).getState() == ((mapProp2 >> i) % 2).byteValue())) {
            countMiss++
         }
      }

      if (countMiss > 0) {
         MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowEffect("quest/party/wrong_kor"))
         MasterBroadcaster.getInstance().sendToAllInMap(map, new PlaySound("Party1/Failed"))

         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("LEVERS_MISPLACED").with(countMiss))
         return false
      }

      MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowEffect("quest/party/clear"))
      MasterBroadcaster.getInstance().sendToAllInMap(map, new PlaySound("Party1/Clear"))
      pi.getEventInstance().setProperty("jail" + jailn, "0")
   }

   pi.playPortalSound(); pi.warp(pi.getMapId() + 2, 0)
   return true
}

def enterNoMobs(PortalPlayerInteraction pi) {
   MapleMap map = pi.getMap()
   int mobCount = map.countMonster(9300044)

   if (mobCount > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("DEFEAT_THREATS_WITH_LEVERS"))
      return false
   } else {

      pi.playPortalSound(); pi.warp(pi.getMapId() + 2, 0)
      return true
   }
}

def enter(PortalPlayerInteraction pi) {
   boolean ret
   if (leverSequenceExit) {
      ret = enterLeverSequence(pi)
   } else {
      ret = enterNoMobs(pi)
   }

   return ret
}