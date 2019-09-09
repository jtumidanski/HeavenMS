package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoSwordman {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapgoSwordman getMap() {
   getBinding().setVariable("map", new MapgoSwordman())
   return (MapgoSwordman) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}