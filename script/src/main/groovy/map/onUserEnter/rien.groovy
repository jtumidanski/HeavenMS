package map.onUserEnter


import scripting.map.MapScriptMethods

class Maprien {

   static def start(MapScriptMethods ms) {
      if (ms.isQuestCompleted(21101) && ms.containsAreaInfo((short) 21019, "miss=o;arr=o;helper=clear")) {
         ms.updateAreaInfo((short) 21019, "miss=o;arr=o;ck=1;helper=clear")
      }
      ms.unlockUI()
   }
}

Maprien getMap() {
   getBinding().setVariable("map", new Maprien())
   return (Maprien) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}