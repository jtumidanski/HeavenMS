package portal

import client.MapleBuffStat
import client.MapleCharacter
import scripting.portal.PortalPlayerInteraction

static def isTigunMorphed(MapleCharacter ch) {
   return ch.getBuffSource(MapleBuffStat.MORPH) == 2210005
}

static def enter(PortalPlayerInteraction pi) {
   if(isTigunMorphed(pi.getPlayer())) {
      return false
   } else {
      pi.playPortalSound(); pi.warp(260000300, 7)
      pi.message("You, intruder! You don't have permission to roam the palace! Get out!!")
      return true
   }
}