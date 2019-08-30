package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor6109018 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "The Mage Sigil has been activated!")
         eim.setIntProperty("glpq4", eim.getIntProperty("glpq4") + 1)
         if (eim.getIntProperty("glpq4") == 5) { //all 5 done
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "The Antellion grants you access to the next portal! Proceed!")

            eim.showClearEffect(610030400, "4pt", 2)
            eim.giveEventPlayersStageReward(4)
         }
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor6109018 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109018(rm: rm))
   return (Reactor6109018) getBinding().getVariable("reactor")
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