package map.onUserEnter


import scripting.map.MapScriptMethods

class Map130030000 {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().updateQuestInfo(20010, "1")
   }
}

Map130030000 getMap() {
   getBinding().setVariable("map", new Map130030000())
   return (Map130030000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}