package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor40 {

   static def start(MapScriptMethods ms) {
      ms.openNpc(2159012)
   }
}

MapResi_tutor40 getMap() {
   getBinding().setVariable("map", new MapResi_tutor40())
   return (MapResi_tutor40) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}