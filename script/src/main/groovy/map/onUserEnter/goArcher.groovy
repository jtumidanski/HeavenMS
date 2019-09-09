package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoArcher {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapgoArcher getMap() {
   getBinding().setVariable("map", new MapgoArcher())
   return (MapgoArcher) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}