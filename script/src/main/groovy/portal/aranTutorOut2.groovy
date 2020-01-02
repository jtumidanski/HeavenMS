package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.teachSkill(20000014, (byte) 0, (byte) -1, -1)
   pi.teachSkill(20000015, (byte) 0, (byte) -1, -1)
   pi.teachSkill(20000014, (byte) 1, (byte) 0, -1)
   pi.teachSkill(20000015, (byte) 1, (byte) 0, -1)
   pi.playPortalSound(); pi.warp(914000210, 1)
   return true
}