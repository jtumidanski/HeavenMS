package map.onUserEnter


import scripting.map.MapScriptMethods

class MapRienArrow {

   static def start(MapScriptMethods ms) {
      if (ms.containsAreaInfo((short) 21019, "miss=o;helper=clear")) {
         ms.updateAreaInfo((short) 21019, "miss=o;arr=o;helper=clear")
         ms.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3")
      }
   }
}

MapRienArrow getMap() {
   getBinding().setVariable("map", new MapRienArrow())
   return (MapRienArrow) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}