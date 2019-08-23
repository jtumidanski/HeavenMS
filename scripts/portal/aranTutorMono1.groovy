package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 21002, "mo2=o")) {
      return false
   }
   pi.playSound("Aran/balloon")
   pi.updateAreaInfo((short) 21002, "mo1=o;mo2=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon2")
   return true
}