package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor6109020 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         eim.dropMessage(6, "The Pirate Sigil has been activated!")
         eim.setIntProperty("glpq4", eim.getIntProperty("glpq4") + 1)
         if (eim.getIntProperty("glpq4") == 5) { //all 5 done
            eim.dropMessage(6, "The Antellion grants you access to the next portal! Proceed!")

            eim.showClearEffect(610030400, "4pt", 2)
            eim.giveEventPlayersStageReward(4)
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

Reactor6109020 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109020(rm: rm))
   return (Reactor6109020) getBinding().getVariable("reactor")
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