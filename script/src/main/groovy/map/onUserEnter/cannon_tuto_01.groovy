package map.onUserEnter


import scripting.map.MapScriptMethods

class MapCannonTutorial01 {

   static def start(MapScriptMethods ms) {
      ms.setDirection(0)
      ms.setDirectionStatus(true)
      ms.lockUI()
      ms.startDirection("cannon_tuto_02")
   }
}

MapCannonTutorial01 getMap() {
   getBinding().setVariable("map", new MapCannonTutorial01())
   return (MapCannonTutorial01) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}