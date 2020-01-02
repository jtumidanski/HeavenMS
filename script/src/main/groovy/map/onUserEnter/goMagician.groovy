package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoMagician {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapGoMagician getMap() {
   getBinding().setVariable("map", new MapGoMagician())
   return (MapGoMagician) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}