package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo1010100 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010100")
   }
}

MapGo1010100 getMap() {
   getBinding().setVariable("map", new MapGo1010100())
   return (MapGo1010100) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}