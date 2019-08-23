package reactor


import scripting.reactor.ReactorActionManager


class Reactor2111000 {
   ReactorActionManager rm

   def act() {
      rm.playerMessage(5, "Oh noes! Monsters in the chest!")
      rm.spawnMonster(9300004,3)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2111000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2111000(rm: rm))
   return (Reactor2111000) getBinding().getVariable("reactor")
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