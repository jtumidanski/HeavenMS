package map.onUserEnter


import scripting.map.MapScriptMethods

class Map914000100 {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().updateQuestInfo(21000, "1")
   }
}

Map914000100 getMap() {
   getBinding().setVariable("map", new Map914000100())
   return (Map914000100) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}