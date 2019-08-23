package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoRogue {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapgoRogue getMap() {
   getBinding().setVariable("map", new MapgoRogue())
   return (MapgoRogue) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}