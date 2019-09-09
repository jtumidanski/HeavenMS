package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor80 {

   static def start(MapScriptMethods ms) {
      ms.setDirectionMode(false)
   }
}

MapResi_tutor80 getMap() {
   getBinding().setVariable("map", new MapResi_tutor80())
   return (MapResi_tutor80) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}