package reactor


import scripting.reactor.ReactorActionManager


class Reactor1050000 {
   ReactorActionManager rm

   def act() {
      if (Math.random() > 0.7) {
         rm.dropItems()
      } else {
         rm.warp(105090200, 0)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor1050000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1050000(rm: rm))
   return (Reactor1050000) getBinding().getVariable("reactor")
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