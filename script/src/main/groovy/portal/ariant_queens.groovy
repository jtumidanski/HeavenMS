package portal

import client.MapleBuffStat
import client.MapleCharacter
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def isTigunMorphed(MapleCharacter ch) {
   return ch.getBuffSource(MapleBuffStat.MORPH) == 2210005
}

boolean enter(PortalPlayerInteraction pi) {
   if(isTigunMorphed(pi.getPlayer())) {
      return false
   } else {
      pi.playPortalSound(); pi.warp(260000300, 7)
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PALACE_INTRUDER"))
      return true
   }
}