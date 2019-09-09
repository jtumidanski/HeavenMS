package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapcannon_tuto_direction {

   static def start(MapScriptMethods ms) {
      ms.setDirectionStatus(true)
      ms.showIntro("Effect/Direction4.img/cannonshooter/Scene00")
      ms.showIntro("Effect/Direction4.img/cannonshooter/out00")
   }
}

Mapcannon_tuto_direction getMap() {
   getBinding().setVariable("map", new Mapcannon_tuto_direction())
   return (Mapcannon_tuto_direction) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}