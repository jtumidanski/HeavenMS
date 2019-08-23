package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor6109009 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         eim.dropMessage(6, "A weapon has been restored to the Relic of Mastery!")
         eim.setIntProperty("glpq5", eim.getIntProperty("glpq5") + 1)
         if (eim.getIntProperty("glpq5") == 5) { //all 5 done
            eim.dropMessage(6, "The Antellion grants you access to the next portal! Proceed!")

            eim.showClearEffect(610030500, "5pt", 2)
            eim.giveEventPlayersStageReward(5)
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

Reactor6109009 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109009(rm: rm))
   return (Reactor6109009) getBinding().getVariable("reactor")
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