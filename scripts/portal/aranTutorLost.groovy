package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 21002, "fin=o")) {
      return false
   }
   pi.updateAreaInfo((short) 21002, "cmd=o;normal=o;arr0=o;arr1=o;arr2=o;arr3=o;fin=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o")
   pi.showIntro("Effect/Direction1.img/aranTutorial/ClickChild")
   return true
}