package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor6109024 {
   ReactorActionManager rm
   String fid = "glpq_f4"

   def action() {
      String[] flames = ["d6", "d7", "e6", "e7", "f6", "f7"]
      for (int i = 0; i < flames.length; i++) {
         rm.getMap().toggleEnvironment(flames[i])
      }
   }


   def act() {

   }

   def hit() {

   }

   def touch() {
      EventInstanceManager eim = rm.getEventInstance()

      if (eim.getIntProperty(fid) == 0) {
         action()
      }
      eim.setIntProperty(fid, eim.getIntProperty(fid) + 1)
   }

   def untouch() {
      EventInstanceManager eim = rm.getEventInstance()

      if (eim.getIntProperty(fid) == 1) {
         action()
      }
      eim.setIntProperty(fid, eim.getIntProperty(fid) - 1)
   }
}

Reactor6109024 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109024(rm: rm))
   return (Reactor6109024) getBinding().getVariable("reactor")
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