package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapcannon_tuto_01 {

   static def start(MapScriptMethods ms) {
      ms.setDirection(0)
      ms.setDirectionStatus(true)
      ms.lockUI()
      ms.startDirection("cannon_tuto_02")
   }
}

Mapcannon_tuto_01 getMap() {
   getBinding().setVariable("map", new Mapcannon_tuto_01())
   return (Mapcannon_tuto_01) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}