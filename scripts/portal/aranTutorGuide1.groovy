package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 21002, "chain=o")) {
      return false
   }
   pi.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialGuide2")
   pi.message("You can use Consecutive Attacks by pressing the Ctrl key multiple times.")
   pi.updateAreaInfo((short) 21002, "normal=o;arr0=o;arr1=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o")
   return true
}