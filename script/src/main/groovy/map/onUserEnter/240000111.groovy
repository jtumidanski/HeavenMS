package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map240000111 {
   String eventName = "Cabin"
   int toMap = 200090210

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map240000111 getMap() {
   getBinding().setVariable("map", new Map240000111())
   return (Map240000111) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}