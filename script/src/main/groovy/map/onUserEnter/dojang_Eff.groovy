package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapdojang_Eff {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().resetEnteredScript()
      int stage = Math.floor(ms.getPlayer().getMap().getId() / 100) % 100

      ms.getPlayer().showDojoClock()
      if (stage % 6 > 0) {
         int realstage = stage - ((stage / 6) | 0)
         ms.dojoEnergy()

         ms.playSound("Dojang/start")
         ms.showEffect("dojang/start/stage")
         ms.showEffect("dojang/start/number/" + realstage)
      }
   }
}

Mapdojang_Eff getMap() {
   getBinding().setVariable("map", new Mapdojang_Eff())
   return (Mapdojang_Eff) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}