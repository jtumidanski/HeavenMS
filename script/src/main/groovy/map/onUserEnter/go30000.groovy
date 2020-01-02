package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo30000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/30000")
   }
}

MapGo30000 getMap() {
   getBinding().setVariable("map", new MapGo30000())
   return (MapGo30000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}