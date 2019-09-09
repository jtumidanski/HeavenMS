package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoPirate {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapgoPirate getMap() {
   getBinding().setVariable("map", new MapgoPirate())
   return (MapgoPirate) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}