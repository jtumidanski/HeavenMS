package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   //lol nexon does this xD
   pi.teachSkill(20000014, (byte) 0, (byte) -1, -1)
   pi.teachSkill(20000015, (byte) 0, (byte) -1, -1)
   //nexon sends updatePlayerStats MapleStat.AVAILABLESP 0
   pi.teachSkill(20000014, (byte) 1, (byte) 0, -1)
   pi.teachSkill(20000015, (byte) 1, (byte) 0, -1)
   //actually nexon does enableActions here :P
   pi.playPortalSound(); pi.warp(914000210, 1)
   return true
}