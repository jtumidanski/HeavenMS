package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2006001 {
   ReactorActionManager rm

   def act() {
      rm.spawnNpc(2013002)
      rm.getEventInstance().clearPQ()

      rm.getEventInstance().setProperty("statusStg8", "1")

      EventInstanceManager eim = rm.getEventInstance()
      eim.giveEventPlayersExp(3500)
      eim.showClearEffect(true)

      rm.getEventInstance().startEventTimer(5 * 60000) //bonus time
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2006001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2006001(rm: rm))
   return (Reactor2006001) getBinding().getVariable("reactor")
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

def release() {
   getReactor().release()
}