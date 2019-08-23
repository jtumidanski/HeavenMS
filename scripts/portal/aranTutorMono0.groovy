package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 21002, "mo1=o")) {
      return false
   }
   pi.updateAreaInfo((short) 21002, "mo1=o")
   pi.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon1")
   return true
}