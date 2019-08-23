package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 21002, "normal=o")) {
      return false
   }
   pi.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialGuide1")
   pi.message("To use a Regular Attack on monsters, press the Ctrl key.")
   pi.updateAreaInfo((short) 21002, "normal=o;arr0=o;mo1=o;mo2=o;mo3=o")
   return true
}