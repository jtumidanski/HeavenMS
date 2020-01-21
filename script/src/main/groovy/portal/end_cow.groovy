package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(2180) && (pi.hasItem(4031847) || pi.hasItem(4031848) || pi.hasItem(4031849) || pi.hasItem(4031850))) {
      if (pi.hasItem(4031850)) {
         pi.playPortalSound(); pi.warp(120000103)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("COW_END"))
         return false
      }
   } else {
      pi.playPortalSound(); pi.warp(120000103)
      return true
   }
}