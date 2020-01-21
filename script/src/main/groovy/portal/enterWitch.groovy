package portal

import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
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
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SHOULD_NOT_GO_CREEPY"))
      return false
   }
}