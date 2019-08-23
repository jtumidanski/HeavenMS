package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleReactor

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getEventInstance()

         if (!eim.isEventCleared()) {
            if (status == 0) {
               if (eim.getIntProperty("npcShocked") == 0 && cm.haveItem(4001130, 1)) {
                  cm.gainItem(4001130, (short) -1)
                  eim.setIntProperty("npcShocked", 1)

                  cm.sendNext("Oh? You got a letter for me? On times like this, what should it be... Gasp! Something big is going on, guys. Rally yourselves, from now on things will be harder than ever!")
                  eim.dropMessage(6, "Juliet seemed very much in shock after reading Romeo's Letter.")

                  cm.dispose()
               } else if (eim.getIntProperty("statusStg4") == 1) {
                  MapleReactor door = cm.getMap().getReactorByName("jnr3_out3")

                  if (door.getState() == ((byte) 0)) {
                     cm.sendNext("Let me open the door for you.")
                     door.hitReactor(cm.getClient())
                  } else {
                     cm.sendNext("Please hurry, Romeo is in trouble.")
                  }

                  cm.dispose()
               } else if (cm.haveItem(4001134, 1) && cm.haveItem(4001135, 1)) {
                  if (cm.isEventLeader()) {
                     cm.gainItem(4001134, (short) -1)
                     cm.gainItem(4001135, (short) -1)
                     cm.sendNext("Great! You got both Alcadno and Zenumist files at hand. Now we can proceed.")

                     eim.showClearEffect()
                     eim.giveEventPlayersStageReward(4)
                     eim.setIntProperty("statusStg4", 1)

                     cm.getMap().killAllMonsters()
                     cm.getMap().getReactorByName("jnr3_out3").hitReactor(cm.getClient())
                  } else {
                     cm.sendOk("Please let your leader pass the files to me.")
                  }

                  cm.dispose()
               } else {
                  cm.sendYesNo("We must keep fighting to save Romeo, please keep your pace. If you are not feeling so well to continue, your companions and I will understand... So, are you going to retreat?")
               }
            } else {
               cm.warp(926110700)
               cm.dispose()
            }
         } else {
            if (status == 0) {
               if (eim.getIntProperty("escortFail") == 0) {
                  cm.sendNext("Finally, Romeo is safe! Thanks to your efforts, we could save him from the clutches of Yulete, who will now be judged for his rebellion against Magatia. From now on, as he will start rehabilitation, we will keep an eye on his endeavours, making sure he will cause no more troubles on the future.")
               } else {
                  cm.sendNext("Romeo is safe now, although the battle took it's toll on him... Thanks to your efforts, we could save him from the clutches of Yulete, who will now be judged for his rebellion against Magatia. Thank you.")
                  status = 2
               }
            } else if (status == 1) {
               cm.sendNext("Now, please receive this gift as an act of acceptation for our gratitude.")
            } else if (status == 2) {
               if (cm.canHold(4001160)) {
                  cm.gainItem(4001160, (short) 1)

                  if (eim.getIntProperty("normalClear") == 1) {
                     cm.warp(926110600)
                  } else {
                     cm.warp(926110500)
                  }
               } else {
                  cm.sendOk("Make sure you have a space on your ETC inventory.")
               }

               cm.dispose()
            } else {
               cm.warp(926110600)
               cm.dispose()
            }
         }
      }
   }
}

NPC2112005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112005(cm: cm))
   }
   return (NPC2112005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }