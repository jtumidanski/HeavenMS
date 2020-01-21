package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2408003 extends SimpleReactor {
   def act() {

   }

   def touch() {
      if (rm.getPlayer().getEventInstance() != null) {
         rm.getPlayer().getEventInstance().setProperty("summoned", "true")
         rm.getPlayer().getEventInstance().setProperty("canEnter", "false")
      }
      rm.spawnFakeMonster(8800000)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("GIGANTIC_CREATURE"))
      //rm.createMapMonitor(rm.getPlayer().getMap().getId(),"ps00");
      switch (rm.getPlayer().getMap().getId()) {
         case 240060000:
            rm.spawnMonster(8810000, 960, 0)
            break
         case 240060100:
            rm.spawnMonster(8810001, 0, 0) //needs correct positions
            break
      }
   }
}

Reactor2408003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2408003(rm: rm))
   return (Reactor2408003) getBinding().getVariable("reactor")
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