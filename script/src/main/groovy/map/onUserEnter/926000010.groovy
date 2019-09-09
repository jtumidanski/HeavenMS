package map.onUserEnter


import scripting.map.MapScriptMethods
import server.maps.MapleMap

class Map926000010 {

   static def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(926000010)
      map.resetPQ(1)
      return (true)
   }
}

Map926000010 getMap() {
   getBinding().setVariable("map", new Map926000010())
   return (Map926000010) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}