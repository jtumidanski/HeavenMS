package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoArcher {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapGoArcher getMap() {
   getBinding().setVariable("map", new MapGoArcher())
   return (MapGoArcher) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}