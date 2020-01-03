package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

class Reactor6109027 extends SimpleReactor {
   String fid = "glpq_f7"

   def action() {
      String[] flames = ["g6", "g7", "h6", "h7", "i6", "i7"]
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

Reactor6109027 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109027(rm: rm))
   return (Reactor6109027) getBinding().getVariable("reactor")
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