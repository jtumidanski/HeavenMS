package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22014, "mo30=o")) {
      return false
   }
   pi.updateAreaInfo((short) 22014, "mo30=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon30")
   return true
}