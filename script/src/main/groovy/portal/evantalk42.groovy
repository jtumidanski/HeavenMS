package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22014, "mo42=o")) {
      return false
   }
   pi.updateAreaInfo((short) 22014, "mo30=o;mo40=o;mo41=o;mo42=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon42")
   return true
}