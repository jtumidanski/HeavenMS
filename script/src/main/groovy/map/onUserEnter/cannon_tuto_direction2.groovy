package map.onUserEnter


import scripting.map.MapScriptMethods

class MapCannonTutorialDirection2 {

   static def start(MapScriptMethods ms) {
      ms.playSound("cannonshooter/bang")
      ms.setDirectionStatus(true)
      ms.showIntro("Effect/Direction4.img/cannonshooter/Scene01")
      ms.showIntro("Effect/Direction4.img/cannonshooter/out02")
   }
}

MapCannonTutorialDirection2 getMap() {
   getBinding().setVariable("map", new MapCannonTutorialDirection2())
   return (MapCannonTutorialDirection2) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}