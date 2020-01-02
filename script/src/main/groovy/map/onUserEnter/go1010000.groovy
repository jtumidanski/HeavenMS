package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo1010000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010000")
   }
}

MapGo1010000 getMap() {
   getBinding().setVariable("map", new MapGo1010000())
   return (MapGo1010000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}