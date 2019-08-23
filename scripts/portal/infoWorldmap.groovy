package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInfo("UI/tutorial.img/26")
   pi.blockPortal()
   return true
}