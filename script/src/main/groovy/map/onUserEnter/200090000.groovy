package map.onUserEnter

import scripting.map.MapScriptMethods
import server.maps.MapleMap
import tools.MaplePacketCreator
import tools.PacketCreator
import tools.packet.field.effect.MusicChange

class Map200090000 {
   int mapId = 200090000

   def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(mapId)

      if (map.getDocked()) {
         PacketCreator.announce(ms.getClient(), new MusicChange("Bgm04/ArabPirate"))
         ms.getClient().announce(MaplePacketCreator.crogBoatPacket(true))
      }

      return (true)
   }
}

Map200090000 getMap() {
   getBinding().setVariable("map", new Map200090000())
   return (Map200090000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}