package map.onUserEnter


import scripting.map.MapScriptMethods

class MapAranDirection {

   static def start(MapScriptMethods ms) {
      ms.displayAranIntro()
   }
}

MapAranDirection getMap() {
   getBinding().setVariable("map", new MapAranDirection())
   return (MapAranDirection) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}