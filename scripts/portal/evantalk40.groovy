package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22014, "mo40=o")) {
      return false
   }
   pi.updateAreaInfo((short) 22014, "mo30=o;mo40=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon40")
   return true
}