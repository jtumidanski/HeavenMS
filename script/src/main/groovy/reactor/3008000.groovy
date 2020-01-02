package reactor

import client.MapleCharacter
import scripting.reactor.ReactorActionManager


class Reactor3008000 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      MapleCharacter[] players = rm.getMap().getAllPlayers()
      for (int i = 0; i < players.length; i++) {
         rm.giveCharacterExp(52000, players[i])
      }
   }

   def touch() {

   }

   def release() {

   }
}

Reactor3008000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor3008000(rm: rm))
   return (Reactor3008000) getBinding().getVariable("reactor")
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