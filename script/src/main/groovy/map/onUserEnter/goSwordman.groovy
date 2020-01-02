package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoSwordman {

   static def start(MapScriptMethods ms) {
      ms.startExplorerExperience()
   }
}

MapGoSwordman getMap() {
   getBinding().setVariable("map", new MapGoSwordman())
   return (MapGoSwordman) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}