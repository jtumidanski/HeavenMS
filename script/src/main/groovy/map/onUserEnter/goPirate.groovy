package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoPirate {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapGoPirate getMap() {
   getBinding().setVariable("map", new MapGoPirate())
   return (MapGoPirate) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}