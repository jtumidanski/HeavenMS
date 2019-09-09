package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo1010100 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010100")
   }
}

Mapgo1010100 getMap() {
   getBinding().setVariable("map", new Mapgo1010100())
   return (Mapgo1010100) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}