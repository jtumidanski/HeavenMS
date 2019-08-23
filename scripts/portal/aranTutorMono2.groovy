package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 21002, "mo3=o")) {
      return false
   }
   pi.updateAreaInfo((short) 21002, "mo1=o;mo2=o;mo3=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon3")
   return true
}