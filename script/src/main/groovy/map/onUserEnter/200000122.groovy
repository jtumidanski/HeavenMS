package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map200000122 {
   String eventName = "Trains"
   int toMap = 200090100

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map200000122 getMap() {
   getBinding().setVariable("map", new Map200000122())
   return (Map200000122) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}