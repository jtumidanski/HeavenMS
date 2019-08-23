package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestCompleted(3360)) {
      return doorCross(pi)
   } else if(pi.isQuestStarted(3360)) {
      if(pi.getQuestProgress(3360, 1) == 0) {
         pi.openNpc(2111024, "MagatiaPassword")
         return false
      } else {
         return doorCross(pi)
      }
   } else {
      pi.message("This door is locked.")
      return false
   }
}

static def doorCross(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(261030000, "sp_" + ((pi.getMapId() == 261010000) ? "jenu" : "alca"))
   return true
}