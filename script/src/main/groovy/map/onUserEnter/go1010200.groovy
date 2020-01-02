package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo1010200 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010200")
   }
}

MapGo1010200 getMap() {
   getBinding().setVariable("map", new MapGo1010200())
   return (MapGo1010200) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}