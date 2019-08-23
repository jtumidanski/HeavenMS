package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor6109014 {
   ReactorActionManager rm
   String fid = "glpq_f0"

   def act() {

   }

   def hit() {

   }

   def action() { //flame0, im assuming this is topleft
      String[] flames = ["a1", "a2", "b1", "b2", "c1", "c2"]
      for (int i = 0; i < flames.length; i++) {
         rm.getMap().toggleEnvironment(flames[i])
      }
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

Reactor6109014 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109014(rm: rm))
   return (Reactor6109014) getBinding().getVariable("reactor")
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