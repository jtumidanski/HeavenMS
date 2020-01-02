package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.teachSkill(20000016, (byte) 0, (byte) -1, -1)
   pi.teachSkill(20000016, (byte) 1, (byte) 0, -1)
   pi.playPortalSound(); pi.warp(914000220, 1)
   return true
}