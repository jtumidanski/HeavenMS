package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor9208000 {
   ReactorActionManager rm

   static def padWithZeroes(String n, width) {
      while (n.length() < width) n = '0' + n
      return n
   }

   def act() {
      EventInstanceManager eim = rm.getPlayer().getEventInstance()
      if (eim != null) {
         String status = eim.getProperty("stage1status")
         if (status != null && status != "waiting") {
            int stage = (eim.getProperty("stage1phase")).toInteger()
            if (status == "display") {
               if (!rm.getReactor().isRecentHitFromAttack()) {
                  String prevCombo = eim.getProperty("stage1combo")

                  String n = "" + (rm.getReactor().getObjectId() % 1000)
                  prevCombo += padWithZeroes(n, 3)

                  eim.setProperty("stage1combo", prevCombo)
                  if (prevCombo.length() == (3 * (stage + 3))) { //end of displaying
                     eim.setProperty("stage1status", "active")
                     rm.mapMessage(5, "The combo has been displayed; Proceed with caution.")
                     eim.setProperty("stage1guess", "")
                  }
               }
            } else { //active
               String prevGuess = "" + eim.getProperty("stage1guess")
               if (prevGuess.length() != (3 * (stage + 3))) {
                  String n = "" + (rm.getReactor().getObjectId() % 1000)
                  prevGuess += padWithZeroes(n, 3)

                  eim.setProperty("stage1guess", prevGuess)
               }
            }
         }
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor9208000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9208000(rm: rm))
   return (Reactor9208000) getBinding().getVariable("reactor")
}

def act() {
   getReactor().act()
}

def hit() {
   getReactor().hit()
}

def touch() {
   getReactor().touch()
}

def untouch() {
   getReactor().untouch()
}