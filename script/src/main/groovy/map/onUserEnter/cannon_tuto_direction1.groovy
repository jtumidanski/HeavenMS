package map.onUserEnter


import scripting.map.MapScriptMethods

class MapCannonTutorialDirection1 {

   static def start(MapScriptMethods ms) {
      ms.playSound("cannonshooter/flying")
      ms.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/0", 9000, 0, 0, 0, -1)
      ms.sendDirectionInfo(1, 1500)
      ms.setDirectionStatus(true)
   }
}

MapCannonTutorialDirection1 getMap() {
   getBinding().setVariable("map", new MapCannonTutorialDirection1())
   return (MapCannonTutorialDirection1) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}