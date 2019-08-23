package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getJob().getId() == 2000 && !pi.isQuestCompleted(21014)) {
      pi.showInfoText("The town of Rien is to the right. Take the portal on the right and go into town to meet Lilin.")
      return false
   } else {
      pi.playPortalSound()
      pi.warp(140010100, 2)
      return true
   }
}