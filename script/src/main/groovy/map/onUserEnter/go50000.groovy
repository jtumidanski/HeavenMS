package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo50000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/50000")
   }
}

MapGo50000 getMap() {
   getBinding().setVariable("map", new MapGo50000())
   return (MapGo50000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}