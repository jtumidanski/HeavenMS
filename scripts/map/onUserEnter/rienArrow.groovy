package map.onUserEnter


import scripting.map.MapScriptMethods

class MaprienArrow {

   static def start(MapScriptMethods ms) {
      if (ms.containsAreaInfo((short) 21019, "miss=o;helper=clear")) {
         ms.updateAreaInfo((short) 21019, "miss=o;arr=o;helper=clear")
         ms.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3")
      }
   }
}

MaprienArrow getMap() {
   getBinding().setVariable("map", new MaprienArrow())
   return (MaprienArrow) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}