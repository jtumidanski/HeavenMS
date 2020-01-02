package map.onUserEnter


import scripting.map.MapScriptMethods

class MapCannonTutorialDirection {

   static def start(MapScriptMethods ms) {
      ms.setDirectionStatus(true)
      ms.showIntro("Effect/Direction4.img/cannonshooter/Scene00")
      ms.showIntro("Effect/Direction4.img/cannonshooter/out00")
   }
}

MapCannonTutorialDirection getMap() {
   getBinding().setVariable("map", new MapCannonTutorialDirection())
   return (MapCannonTutorialDirection) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}