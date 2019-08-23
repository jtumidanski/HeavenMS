package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   //lol nexon does this xD
   pi.teachSkill(20000016, (byte) 0, (byte) -1, -1)
   //nexon sends updatePlayerStats MapleStat.AVAILABLESP 0
   pi.teachSkill(20000016, (byte) 1, (byte) 0, -1)
   //actually nexon does enableActions here :P
   pi.playPortalSound(); pi.warp(914000220, 1)
   return true
}