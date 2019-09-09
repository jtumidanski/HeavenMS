package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22014, "egg=o")) {
      return false
   }
   pi.updateAreaInfo((short) 22014, "egg=o;mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o")
   pi.showInfo("UI/tutorial/evan/8/0")
   return true
}