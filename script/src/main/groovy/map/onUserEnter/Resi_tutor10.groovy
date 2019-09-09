package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor10 {

   static def start(MapScriptMethods ms) {
      ms.setStandAloneMode(true)
   }
}

MapResi_tutor10 getMap() {
   getBinding().setVariable("map", new MapResi_tutor10())
   return (MapResi_tutor10) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}