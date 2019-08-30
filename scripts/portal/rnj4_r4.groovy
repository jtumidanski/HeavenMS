package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   int area = eim.getIntProperty("statusStg5")
   int reg = 3

   if((area >> reg) % 2 == 0) {
      area |= (1 << reg)
      eim.setIntProperty("statusStg5", area)

      pi.playPortalSound()
      pi.warp(926100301 + reg, 0) //next
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "This room is already being explored.")
      return false
   }
}