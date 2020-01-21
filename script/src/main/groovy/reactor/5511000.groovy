package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor5511000 extends SimpleReactor {
   def act() {
      if (rm.getReactor().getMap().getMonsterById(9420542) == null) {
         rm.schedule("summonBoss", 3200)
      }
   }

   def summonBoss() {
      rm.spawnMonster(9420542, -527, 637)
      rm.changeMusic("Bgm09/TimeAttack")
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("TARGA_SUMMONED"))
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

def release() {
   getReactor().release()
}