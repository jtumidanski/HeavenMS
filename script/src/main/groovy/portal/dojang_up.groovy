package portal

import constants.game.GameConstants
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getMonsterById(9300216) != null) {
      pi.goDojoUp()
      pi.getPlayer().getMap().setReactorState()
      int stage = Math.floor(pi.getPlayer().getMapId() / 100) % 100
      if ((stage - (stage / 6) | 0) == pi.getPlayer().getVanquisherStage() && !GameConstants.isDojoPartyArea(pi.getPlayer().getMapId())) // we can also try 5 * stage / 6 | 0 + 1
      {
         pi.getPlayer().setVanquisherKills(pi.getPlayer().getVanquisherKills() + 1)
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "There are still some monsters remaining.")
   }
   pi.enableActions()
   return true
}