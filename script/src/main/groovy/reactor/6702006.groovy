package reactor


import scripting.reactor.ReactorActionManager


class Reactor6702006 {
   ReactorActionManager rm

   def act() {
      int rand = Math.floor(Math.random() * 4).intValue()
      if (rand < 1) {
         rand = 1
      }
      //We'll make it drop a lot of crap :D
      for (int i = 0; i < rand; i++) {
         rm.sprayItems(true, 1, 30, 60, 15)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor6702006 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6702006(rm: rm))
   return (Reactor6702006) getBinding().getVariable("reactor")
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