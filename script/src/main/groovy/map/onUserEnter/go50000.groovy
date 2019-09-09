package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo50000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/50000")
   }
}

Mapgo50000 getMap() {
   getBinding().setVariable("map", new Mapgo50000())
   return (Mapgo50000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}