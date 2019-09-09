package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoMagician {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapgoMagician getMap() {
   getBinding().setVariable("map", new MapgoMagician())
   return (MapgoMagician) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}