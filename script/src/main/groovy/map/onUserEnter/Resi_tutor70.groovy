package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor70 {

   static def start(MapScriptMethods ms) {
      ms.setDirectionMode(true)
      ms.showIntro("Effect/Direction4.img/Resistance/TalkJ")
   }
}

MapResi_tutor70 getMap() {
   getBinding().setVariable("map", new MapResi_tutor70())
   return (MapResi_tutor70) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}