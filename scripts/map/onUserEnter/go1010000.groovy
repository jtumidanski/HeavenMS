package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo1010000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010000")
   }
}

Mapgo1010000 getMap() {
   getBinding().setVariable("map", new Mapgo1010000())
   return (Mapgo1010000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}