package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(1041)) {
      pi.playPortalSound(); pi.warp(1010100, 4)
   } else if (pi.isQuestStarted(1042)) {
      pi.playPortalSound(); pi.warp(1010200, 4)
   } else if (pi.isQuestStarted(1043)) {
      pi.playPortalSound(); pi.warp(1010300, 4)
   } else if (pi.isQuestStarted(1044)) {
      pi.playPortalSound(); pi.warp(1010400, 4)
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MAI_TRAINING_REQUIREMENT"))
      return false
   }
   return true
}