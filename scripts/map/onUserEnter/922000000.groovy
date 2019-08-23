package map.onUserEnter


import scripting.map.MapScriptMethods
import server.maps.MapleMap

class Map922000000 {

   static def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(922000000)
      map.clearDrops()
      map.resetReactors()
      map.shuffleReactors()

      return (true)
   }
}

Map922000000 getMap() {
   getBinding().setVariable("map", new Map922000000())
   return (Map922000000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}