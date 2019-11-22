package map.onUserEnter


import scripting.map.MapScriptMethods

class Maphighposition {
   def start(MapScriptMethods ms) {
      ms.touchTheSky()
   }
}

Maphighposition getMap() {
   getBinding().setVariable("map", new Maphighposition())
   return (Maphighposition) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}