package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor6109001 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         int mapId = rm.getMap().getId()

         if (mapId == 610030200) {
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "The Archer Sigil has been activated!")
            eim.setIntProperty("glpq2", eim.getIntProperty("glpq2") + 1)
            if (eim.getIntProperty("glpq2") == 5) { //all 5 done
               MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "The Antellion grants you access to the next portal! Proceed!")

               eim.showClearEffect(mapId, "2pt", 2)
               eim.giveEventPlayersStageReward(2)
            }
         } else if (mapId == 610030300) {
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "The Archer Sigil has been activated! You hear gears turning! The Menhir Defense System is active! Run!")
            eim.setIntProperty("glpq3", eim.getIntProperty("glpq3") + 1)
            rm.getMap().moveEnvironment("menhir1", 1)
            rm.getMap().moveEnvironment("menhir2", 1)
            if (eim.getIntProperty("glpq3") == 5 && eim.getIntProperty("glpq3_p") == 5) {
               MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "The Antellion grants you access to the next portal! Proceed!")

               eim.showClearEffect(mapId, "3pt", 2)
               eim.giveEventPlayersStageReward(3)
            }
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

Reactor6109001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109001(rm: rm))
   return (Reactor6109001) getBinding().getVariable("reactor")
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