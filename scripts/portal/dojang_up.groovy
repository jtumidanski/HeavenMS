package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getMonsterById(9300216) != null) {
      pi.goDojoUp()
      pi.getPlayer().getMap().setReactorState()
      int stage = Math.floor(pi.getPlayer().getMapId() / 100) % 100
      if ((stage - (stage / 6) | 0) == pi.getPlayer().getVanquisherStage() && !pi.getPlayer().getDojoParty()) // we can also try 5 * stage / 6 | 0 + 1
      {
         pi.getPlayer().setVanquisherKills(pi.getPlayer().getVanquisherKills() + 1)
      }
   } else {
      pi.getPlayer().message("There are still some monsters remaining.")
   }
   pi.enableActions()
   return true
}