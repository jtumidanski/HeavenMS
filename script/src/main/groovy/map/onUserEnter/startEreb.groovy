package map.onUserEnter


import scripting.map.MapScriptMethods

class MapstartEreb {

   def start(MapScriptMethods ms) {
      if (ms.getPlayer().getJob().getId() == 1000 && ms.getPlayer().getLevel() >= 10) {
         ms.unlockUI()
      }
   }
}

MapstartEreb getMap() {
   getBinding().setVariable("map", new MapstartEreb())
   return (MapstartEreb) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}