package reactor


import scripting.reactor.ReactorActionManager


class Reactor6829000 {
   ReactorActionManager rm

   def act() {
      rm.playerMessage(5, "Enjoy Halloween!")
      rm.spawnMonster(9400202, 10)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor6829000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6829000(rm: rm))
   return (Reactor6829000) getBinding().getVariable("reactor")
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