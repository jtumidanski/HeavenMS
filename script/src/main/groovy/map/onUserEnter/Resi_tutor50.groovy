package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor50 {

   static def start(MapScriptMethods ms) {
      ms.setDirectionMode(false)
      ms.openNpc(2159006)
   }
}

MapResi_tutor50 getMap() {
   getBinding().setVariable("map", new MapResi_tutor50())
   return (MapResi_tutor50) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}