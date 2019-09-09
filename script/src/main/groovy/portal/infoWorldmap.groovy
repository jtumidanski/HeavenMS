package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.showInfo("UI/tutorial.img/26")
   pi.blockPortal()
   return true
}