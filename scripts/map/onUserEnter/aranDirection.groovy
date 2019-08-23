package map.onUserEnter


import scripting.map.MapScriptMethods

class MaparanDirection {

   static def start(MapScriptMethods ms) {
      ms.displayAranIntro()
   }
}

MaparanDirection getMap() {
   getBinding().setVariable("map", new MaparanDirection())
   return (MaparanDirection) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}