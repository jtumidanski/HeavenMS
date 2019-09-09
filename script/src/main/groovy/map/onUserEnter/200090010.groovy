package map.onUserEnter


import scripting.map.MapScriptMethods
import server.maps.MapleMap
import tools.MaplePacketCreator

class Map200090010 {
   int mapId = 200090010

   def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(mapId)

      if (map.getDocked()) {
         ms.getClient().announce(MaplePacketCreator.musicChange("Bgm04/ArabPirate"))
         ms.getClient().announce(MaplePacketCreator.crogBoatPacket(true))
      }

      return (true)
   }
}

Map200090010 getMap() {
   getBinding().setVariable("map", new Map200090010())
   return (Map200090010) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}