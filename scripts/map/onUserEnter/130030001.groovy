package map.onUserEnter


import scripting.map.MapScriptMethods

class Map130030001 {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().updateQuestInfo(20010, "1")
   }
}

Map130030001 getMap() {
   getBinding().setVariable("map", new Map130030001())
   return (Map130030001) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}