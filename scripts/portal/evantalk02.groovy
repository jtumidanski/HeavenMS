package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22013, "mo02=o")) {
      return false
   }
   pi.updateAreaInfo((short) 22013, "dt00=o;mo00=o;mo01=o;mo02=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon02")
   return true
}