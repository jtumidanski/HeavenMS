package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapcannon_tuto_direction2 {

   static def start(MapScriptMethods ms) {
      ms.playSound("cannonshooter/bang")
      ms.setDirectionStatus(true)
      ms.showIntro("Effect/Direction4.img/cannonshooter/Scene01")
      ms.showIntro("Effect/Direction4.img/cannonshooter/out02")
   }
}

Mapcannon_tuto_direction2 getMap() {
   getBinding().setVariable("map", new Mapcannon_tuto_direction2())
   return (Mapcannon_tuto_direction2) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}