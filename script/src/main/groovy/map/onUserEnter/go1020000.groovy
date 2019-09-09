package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo1020000 {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.mapEffect("maplemap/enter/1020000")
   }
}

Mapgo1020000 getMap() {
   getBinding().setVariable("map", new Mapgo1020000())
   return (Mapgo1020000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}