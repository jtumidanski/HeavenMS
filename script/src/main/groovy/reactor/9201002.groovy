package reactor


import scripting.reactor.ReactorActionManager


class Reactor9201002 {
   ReactorActionManager rm

   def act() {
      rm.changeMusic("Bgm10/Eregos")
      rm.spawnMonster(9300028)
      rm.spawnMonster(9300031, 130, 90)
      rm.spawnMonster(9300032, 540, 90)
      rm.spawnMonster(9300029, 130, 150)
      rm.spawnMonster(9300030, 540, 150)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor9201002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9201002(rm: rm))
   return (Reactor9201002) getBinding().getVariable("reactor")
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