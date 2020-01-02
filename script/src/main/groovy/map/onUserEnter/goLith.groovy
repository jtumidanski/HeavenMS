package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoLith {

   static def start(MapScriptMethods ms) {
      ms.goLith()
   }
}

MapGoLith getMap() {
   getBinding().setVariable("map", new MapGoLith())
   return (MapGoLith) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}