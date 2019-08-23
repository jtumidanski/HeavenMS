package reactor


import scripting.reactor.ReactorActionManager


class Reactor2508000 {
   ReactorActionManager rm

   def act() {
      if (rm.getPlayer().getMap().getId() / 100 % 100 != 38) {
         rm.warp(rm.getPlayer().getMap().getId() + 100)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2508000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2508000(rm: rm))
   return (Reactor2508000) getBinding().getVariable("reactor")
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