package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo1000000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1000000")
   }
}

Mapgo1000000 getMap() {
   getBinding().setVariable("map", new Mapgo1000000())
   return (Mapgo1000000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}