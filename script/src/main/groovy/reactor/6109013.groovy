package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor6109013 extends SimpleReactor {
   def fid = "glpq_s"

   def act() {

   }

   def action() {
      EventInstanceManager eim = rm.getEventInstance()

      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("STIRGES_DISAPPEARED"))
      rm.getMap().killAllMonsters()
      eim.setIntProperty(fid, 777)
   }

   def touch() {
      EventInstanceManager eim = rm.getEventInstance()

      if (eim.getIntProperty(fid) == 5) {
         action()
      }
      eim.setIntProperty(fid, eim.getIntProperty(fid) + 1)
   }

   def release() {
      EventInstanceManager eim = rm.getEventInstance()

      if (eim.getIntProperty(fid) == 5) {
         action()
      }
      eim.setIntProperty(fid, eim.getIntProperty(fid) - 1)
   }
}

Reactor6109013 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109013(rm: rm))
   return (Reactor6109013) getBinding().getVariable("reactor")
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