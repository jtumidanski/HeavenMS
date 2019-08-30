package portal

import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(20404)) {
      int warpMap

      if (pi.isQuestCompleted(20407)) {
         warpMap = 924010200
      } else if (pi.isQuestCompleted(20406)) {
         warpMap = 924010100
      } else {
         warpMap = 924010000
      }

      pi.playPortalSound()
      pi.warp(warpMap, 1)
      return true


   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "I shouldn't go here.. it's creepy!")
      return false
   }
}