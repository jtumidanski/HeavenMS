package reactor


import scripting.reactor.ReactorActionManager

class Reactor2202002 extends SimpleReactor {
   def act() {
      if (rm.isQuestActive(3238)) {
         rm.warp(922000020, 0)
      } else {
         rm.warp(922000009, 0)
      }
   }
}

Reactor2202002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2202002(rm: rm))
   return (Reactor2202002) getBinding().getVariable("reactor")
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