package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040044 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int curMap, stage

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 922010100) / 100).toInteger() + 1
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

         if (eim.getProperty(stage.toString() + "stageclear") != null) {
            cm.sendNext("Hurry, goto the next stage, the portal is open!")
         } else {
            if (eim.isEventLeader(cm.getPlayer())) {
               int state = eim.getIntProperty("statusStg" + stage)

               if (state == -1) {           // preamble
                  cm.sendOk("Hi. Welcome to the #bBOSS stage#k. Kill the Ratz on that platform to reveal the Alishar, and defeat him!")
                  eim.setProperty("statusStg" + stage, 0)
               } else {                      // check stage completion
                  if (cm.haveItem(4001023, 1)) {
                     cm.gainItem(4001023, (short) -1)
                     eim.setProperty("statusStg" + stage, 1)

                     List<Integer> list = eim.getClearStageBonus(stage)
                     // will give bonus exp & mesos to everyone in the event
                     eim.giveEventPlayersExp(list.get(0))
                     eim.giveEventPlayersMeso(list.get(1))

                     eim.setProperty(stage + "stageclear", "true")
                     eim.showClearEffect(true)

                     eim.clearPQ()
                  } else {
                     cm.sendNext("Please defeat Alishar and bring me his #b#t4001023#.#k")
                  }
               }
            } else {
               cm.sendNext("Please tell your #bParty-Leader#k to come talk to me.")
            }
         }

         cm.dispose()
      }
   }
}

NPC2040044 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040044(cm: cm))
   }
   return (NPC2040044) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }