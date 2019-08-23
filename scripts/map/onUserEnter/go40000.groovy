package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo40000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/40000")
   }
}

Mapgo40000 getMap() {
   getBinding().setVariable("map", new Mapgo40000())
   return (Mapgo40000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}