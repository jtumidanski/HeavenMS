package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor20 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("resistance/tutorialGuide")
   }
}

MapResi_tutor20 getMap() {
   getBinding().setVariable("map", new MapResi_tutor20())
   return (MapResi_tutor20) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}