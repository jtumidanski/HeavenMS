package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo20000 {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.mapEffect("maplemap/enter/20000")
   }
}

Mapgo20000 getMap() {
   getBinding().setVariable("map", new Mapgo20000())
   return (Mapgo20000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}