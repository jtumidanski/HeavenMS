package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.maps.MaplePortal
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int nextMap
   EventInstanceManager eim
   MapleMap target
   MaplePortal targetPortal
   String avail

   if (pi.getPlayer().getMapId() == 240050101) {
      nextMap = 240050102
      eim = pi.getPlayer().getEventInstance()
      target = eim.getMapInstance(nextMap)
      targetPortal = target.getPortal("sp")
      // only let people through if the eim is ready
      avail = eim.getProperty("1stageclear")
      if (avail == null) {
         // do nothing; send message to player
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is Blocking this Door.")
         return false
      } else {
         pi.playPortalSound()
         pi.getPlayer().changeMap(target, targetPortal)
         return true
      }
   } else if (pi.getPlayer().getMapId() == 240050102) {
      nextMap = 240050103
      eim = pi.getPlayer().getEventInstance()
      target = eim.getMapInstance(nextMap)
      targetPortal = target.getPortal("sp")
      // only let people through if the eim is ready
      avail = eim.getProperty("2stageclear")
      if (avail == null) {
         // do nothing; send message to player
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is Blocking this Door.")
         return false
      } else {
         pi.playPortalSound()
         pi.getPlayer().changeMap(target, targetPortal)
         return true
      }
   } else if (pi.getPlayer().getMapId() == 240050103) {
      nextMap = 240050104
      eim = pi.getPlayer().getEventInstance()
      target = eim.getMapInstance(nextMap)
      targetPortal = target.getPortal("sp")
      // only let people through if the eim is ready
      avail = eim.getProperty("3stageclear")
      if (avail == null) {
         // do nothing; send message to player
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is Blocking this Door.")
         return false
      } else {
         pi.playPortalSound()
         pi.getPlayer().changeMap(target, targetPortal)
         return true
      }
   } else if (pi.getPlayer().getMapId() == 240050104) {
      nextMap = 240050105
      eim = pi.getPlayer().getEventInstance()
      target = eim.getMapInstance(nextMap)
      targetPortal = target.getPortal("sp")
      // only let people through if the eim is ready
      avail = eim.getProperty("4stageclear")
      if (avail == null) {
         // do nothing; send message to player
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is Blocking this Door.")
         return false
      } else {
         pi.playPortalSound()
         pi.getPlayer().changeMap(target, targetPortal)
         return true
      }
   } else if (pi.getPlayer().getMapId() == 240050105) {
      nextMap = 240050100
      eim = pi.getPlayer().getEventInstance()
      target = eim.getMapInstance(nextMap)
      targetPortal = target.getPortal("st00")

      avail = eim.getProperty("5stageclear")
      if (avail == null) {
         if (pi.haveItem(4001092) && pi.isEventLeader()) {
            eim.showClearEffect()
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "The leader's key break the seal for a flash...")
            pi.playPortalSound()
            pi.getPlayer().changeMap(target, targetPortal)
            eim.setIntProperty("5stageclear", 1)
            return true
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is blocking this door. Only the leader with the key can lift this seal.")
            return false
         }
      } else {
         pi.playPortalSound()
         pi.getPlayer().changeMap(target, targetPortal)
         return true
      }
   }
   return true
}