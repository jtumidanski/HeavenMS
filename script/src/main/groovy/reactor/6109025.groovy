package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

class Reactor6109025 extends SimpleReactor {
   String fid = "glpq_f5"

   def action() {
      String[] flames = ["g1", "g2", "h1", "h2", "i1", "i2"]
      for (int i = 0; i < flames.length; i++) {
         rm.getMap().toggleEnvironment(flames[i])
      }
   }

   def act() {
   }

   def touch() {
      EventInstanceManager eim = rm.getEventInstance()

      if (eim.getIntProperty(fid) == 0) {
         action()
      }
      eim.setIntProperty(fid, eim.getIntProperty(fid) + 1)
   }

   def release() {
      EventInstanceManager eim = rm.getEventInstance()

      if (eim.getIntProperty(fid) == 1) {
         action()
      }
      eim.setIntProperty(fid, eim.getIntProperty(fid) - 1)
   }
}

Reactor6109025 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109025(rm: rm))
   return (Reactor6109025) getBinding().getVariable("reactor")
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