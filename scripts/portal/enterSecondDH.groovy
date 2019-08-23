package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   int[] maps = [108000600, 108000601, 108000602]
   if (pi.isQuestStarted(20201) || pi.isQuestStarted(20202) || pi.isQuestStarted(20203) || pi.isQuestStarted(20204) || pi.isQuestStarted(20205)) {
      pi.removeAll(4032096)
      pi.removeAll(4032097)
      pi.removeAll(4032098)
      pi.removeAll(4032099)
      pi.removeAll(4032100)

      int rand = Math.floor(Math.random() * maps.length).toInteger()
      pi.playPortalSound(); pi.warp(maps[rand], 0)
      return true
   } else {
      return false
   }
}