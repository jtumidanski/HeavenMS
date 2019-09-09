package map.onUserEnter


import scripting.map.MapScriptMethods
import server.maps.MapleMap
import server.maps.MapleMapObject
import server.maps.MapleReactor

class Map926120300 {

   static def start(MapScriptMethods ms) {
      MapleMap map = ms.getClient().getChannelServer().getMapFactory().getMap(926120300)
      map.resetReactors(getInactiveReactors(map))

      return (true)
   }

   static def getInactiveReactors(MapleMap map) {
      List<MapleReactor> reactors = []

      Iterator<MapleMapObject> iter = map.getReactors().iterator()
      while (iter.hasNext()) {
         MapleReactor mo = (MapleReactor) iter.next()
         if (mo.getState() >= 7) {
            reactors << mo
         }
      }

      return reactors
   }
}

Map926120300 getMap() {
   getBinding().setVariable("map", new Map926120300())
   return (Map926120300) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}