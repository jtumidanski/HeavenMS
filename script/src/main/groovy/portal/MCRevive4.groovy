package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   int portal = 0
   switch (pi.getPlayer().getTeam()) {
      case 0:
         portal = 4
         break
      case 1:
         portal = 3
         break
   }
   pi.warp(980000401, portal)
   pi.playPortalSound()
   return true
}