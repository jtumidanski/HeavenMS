package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22013, "dt01=o")) {
      return false
   }
   pi.mapEffect("evan/dragonTalk01")
   pi.updateAreaInfo((short) 22013, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o")
   return true
}