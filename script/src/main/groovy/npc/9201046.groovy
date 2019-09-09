package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201046 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   boolean debug = false
   int curMap, stage

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 670010200) / 100).intValue() + 1
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()
         if (curMap == 670010750) {
            if (cm.haveItem(4031597, 35)) {
               if (cm.canHold(1102101) && eim.getIntProperty("marriedGroup") == 0) {
                  eim.setIntProperty("marriedGroup", 1)

                  int baseId = (cm.getPlayer().getGender() == 0) ? 1102101 : 1102104
                  int rnd = Math.floor(Math.random() * 3).intValue()
                  cm.gainItem(baseId + rnd)

                  cm.sendNext("Bravo! You are the first to claim the prize for fetching 35 #t4031597#. Take this cape as merit for your feat.")
                  cm.gainItem(4031597, (short) -35)
                  cm.gainExp(4000 * cm.getPlayer().getExpRate())
               } else if (eim.getIntProperty("marriedGroup") == 0) {
                  cm.sendNext("Check if you have a slot available before talking about receiving prizes!")
               } else {
                  cm.sendNext("35 #t4031597#. Nicely done, too bad someone took the prize first. Hurry up to get the last moments of the bonus stage!")
                  cm.gainItem(4031597, (short) -35)
                  cm.gainExp(4000 * cm.getPlayer().getExpRate())
               }
            } else {
               cm.sendNext("To claim a prize here, get to me 35 #t4031597# from the mobs spawned from the boxes. Only the #rfirst player can claim the big prize#k, although others can still claim an EXP boost from this feat. Alternatively, one can choose to #bskip this bonus stage#k and go for the usual one by passing #bthrough the portals#k.")
            }
         } else {
            cm.sendNext("Hurry up to get the last moments of the bonus stage!")
         }

         cm.dispose()
      }
   }
}

NPC9201046 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201046(cm: cm))
   }
   return (NPC9201046) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }