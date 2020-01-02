package map.onUserEnter


import scripting.map.MapScriptMethods

class MapDojang_Eff {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().resetEnteredScript()
      int stage = Math.floor(ms.getPlayer().getMap().getId() / 100) % 100

      ms.getPlayer().showDojoClock()
      if (stage % 6 > 0) {
         int realStage = stage - ((stage / 6) | 0)
         ms.dojoEnergy()

         ms.playSound("Dojang/start")
         ms.showEffect("dojang/start/stage")
         ms.showEffect("dojang/start/number/" + realStage)
      }
   }
}

MapDojang_Eff getMap() {
   getBinding().setVariable("map", new MapDojang_Eff())
   return (MapDojang_Eff) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}