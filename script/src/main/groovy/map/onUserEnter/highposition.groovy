package map.onUserEnter


import scripting.map.MapScriptMethods

class MapHighPosition {
   def start(MapScriptMethods ms) {
      ms.touchTheSky()
   }
}

MapHighPosition getMap() {
   getBinding().setVariable("map", new MapHighPosition())
   return (MapHighPosition) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}