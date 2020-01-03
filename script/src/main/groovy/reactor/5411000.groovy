package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor5411000 extends SimpleReactor {
   def act() {
      rm.changeMusic("Bgm09/TimeAttack")
      rm.spawnMonster(9420513, -146, 225)
      rm.getEventInstance().setIntProperty("boss", 1)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "As you wish, here comes Capt Latanica.")
   }
}

Reactor5411000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor5411000(rm: rm))
   return (Reactor5411000) getBinding().getVariable("reactor")
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