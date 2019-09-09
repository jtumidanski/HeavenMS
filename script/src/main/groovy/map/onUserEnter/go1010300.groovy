package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo1010300 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010300")
   }
}

Mapgo1010300 getMap() {
   getBinding().setVariable("map", new Mapgo1010300())
   return (Mapgo1010300) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}