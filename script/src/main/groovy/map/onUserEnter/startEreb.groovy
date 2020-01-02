package map.onUserEnter


import scripting.map.MapScriptMethods

class MapStartEreb {

   def start(MapScriptMethods ms) {
      if (ms.getPlayer().getJob().getId() == 1000 && ms.getPlayer().getLevel() >= 10) {
         ms.unlockUI()
      }
   }
}

MapStartEreb getMap() {
   getBinding().setVariable("map", new MapStartEreb())
   return (MapStartEreb) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}