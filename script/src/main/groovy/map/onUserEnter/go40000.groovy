package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo40000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/40000")
   }
}

MapGo40000 getMap() {
   getBinding().setVariable("map", new MapGo40000())
   return (MapGo40000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}