package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoRogue {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapGoRogue getMap() {
   getBinding().setVariable("map", new MapGoRogue())
   return (MapGoRogue) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}