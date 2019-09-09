package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoLith {

   static def start(MapScriptMethods ms) {
      ms.goLith()
   }
}

MapgoLith getMap() {
   getBinding().setVariable("map", new MapgoLith())
   return (MapgoLith) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}