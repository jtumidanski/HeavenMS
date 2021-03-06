package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2111001 extends SimpleReactor {
   def act() {
      if (rm.getPlayer().getEventInstance() != null) {
         rm.getPlayer().getEventInstance().setProperty("summoned", "true")
         rm.getPlayer().getEventInstance().setProperty("canEnter", "false")
      }
      rm.changeMusic("Bgm06/FinalFight")
      rm.spawnFakeMonster(8800000)
      for (int i = 8800003; i < 8800011; i++) {
         rm.spawnMonster(i)
      }
      rm.createMapMonitor(280030000, "ps00")
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ZAKUM_SUMMONED"))
   }
}

Reactor2111001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2111001(rm: rm))
   return (Reactor2111001) getBinding().getVariable("reactor")
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