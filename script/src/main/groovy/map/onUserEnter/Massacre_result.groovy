package map.onUserEnter


import scripting.map.MapScriptMethods
import server.partyquest.Pyramid

class MapMassacre_result {

   static def start(MapScriptMethods ms) {
      Pyramid py = ms.getPyramid()
      if (py != null) {
         py.sendScore(ms.getPlayer())
      }
   }
}

MapMassacre_result getMap() {
   getBinding().setVariable("map", new MapMassacre_result())
   return (MapMassacre_result) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}