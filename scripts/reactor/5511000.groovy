package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor5511000 {
   ReactorActionManager rm

   def act() {
      if (rm.getReactor().getMap().getMonsterById(9420542) == null) {
         rm.schedule("summonBoss", 3200)
      }
   }

   def summonBoss() {
      rm.spawnMonster(9420542, -527, 637)
      rm.changeMusic("Bgm09/TimeAttack")
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "Beware! The furious Targa has shown himself!")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor5511000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor5511000(rm: rm))
   return (Reactor5511000) getBinding().getVariable("reactor")
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