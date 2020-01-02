package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo1010300 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010300")
   }
}

MapGo1010300 getMap() {
   getBinding().setVariable("map", new MapGo1010300())
   return (MapGo1010300) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}