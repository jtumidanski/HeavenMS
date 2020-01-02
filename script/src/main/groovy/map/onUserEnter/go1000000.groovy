package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo1000000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1000000")
   }
}

MapGo1000000 getMap() {
   getBinding().setVariable("map", new MapGo1000000())
   return (MapGo1000000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}