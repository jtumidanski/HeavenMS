package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2618000 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      if (rm.getReactor().getState() == ((byte) 6)) {
         EventInstanceManager eim = rm.getEventInstance()

         int done = eim.getIntProperty("statusStg3") + 1
         eim.setIntProperty("statusStg3", done)

         if (done == 3) {
            eim.showClearEffect()
            eim.giveEventPlayersStageReward(3)
            rm.getMap().killAllMonsters()

            String reactname = (eim.getIntProperty("isAlcadno") == 0) ? "rnj2_door" : "jnr2_door"
            rm.getMap().getReactorByName(reactname).hitReactor(rm.getClient())
         }
      }
   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2618000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2618000(rm: rm))
   return (Reactor2618000) getBinding().getVariable("reactor")
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