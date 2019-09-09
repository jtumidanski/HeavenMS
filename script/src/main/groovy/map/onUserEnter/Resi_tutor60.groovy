package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor60 {

   static def start(MapScriptMethods ms) {
      ms.openNpc(2159007)
   }
}

MapResi_tutor60 getMap() {
   getBinding().setVariable("map", new MapResi_tutor60())
   return (MapResi_tutor60) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}