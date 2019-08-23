package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo10000 {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.mapEffect("maplemap/enter/10000")
   }
}

Mapgo10000 getMap() {
   getBinding().setVariable("map", new Mapgo10000())
   return (Mapgo10000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}