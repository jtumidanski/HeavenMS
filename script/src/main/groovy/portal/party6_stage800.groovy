package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   pi.removeAll(4001162)
   pi.removeAll(4001163)
   pi.removeAll(4001164)
   pi.removeAll(4001169)
   pi.removeAll(2270004)

   MapleReactor spring = pi.getMap().getReactorById(3008000)
   if (spring != null && spring.getState() > 0) {
      if (!pi.canHold(4001198, 1)) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("INVENTORY_FULL").with("ETC"))
         return false
      }

      pi.gainItem(4001198, (short) 1)
   }

   pi.playPortalSound(); pi.warp(300030100, 0)
   return true
}