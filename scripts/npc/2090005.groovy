package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2090005 {
   NPCConversationManager cm
   int status = -1
   int slct = -1

   String[] menu = ["Mu Lung", "Orbis", "Herb Town", "Mu Lung"]
   int[] cost = [1500, 1500, 500, 1500]
   EventManager hak
   String display = ""
   String btwmsg

   def start() {
      status = -1
      hak = cm.getEventManager("Hak")
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         } else if (mode == 0) {
            cm.sendNext("OK. If you ever change your mind, please let me know.")
            cm.dispose()
            return
         }
         status++
         if (status == 0) {
            for (int i = 0; i < menu.length; i++) {
               if (cm.getPlayer().getMapId() == 200000141 && i < 1) {
                  display += "\r\n#L" + i + "##b" + menu[i] + "(" + cost[i] + " mesos)#k"
               } else if (cm.getPlayer().getMapId() == 250000100 && i > 0 && i < 3) {
                  display += "\r\n#L" + i + "##b" + menu[i] + "(" + cost[i] + " mesos)#k"
               }
            }
            if (cm.getPlayer().getMapId() == 200000141 || cm.getPlayer().getMapId() == 251000000) {
               btwmsg = "#bOrbis#k to #bMu Lung#k"
            } else if (cm.getPlayer().getMapId() == 250000100) {
               btwmsg = "#bMu Lung#k to #bOrbis#k"
            }
            if (cm.getPlayer().getMapId() == 251000000) {
               cm.sendYesNo("Hello there. How's the traveling so far? I've been transporting other travelers like you to #b" + menu[3] + "#k in no time, and... are you interested? It's not as stable as the ship, so you'll have to hold on tight, but i can get there much faster than the ship. I'll take you there as long as you pay #b" + cost[2] + " mesos#k.")
               status++
            } else if (cm.getPlayer().getMapId() == 250000100) {
               cm.sendSimple("Hello there. How's the traveling so far? I understand that walking on two legs is much harder to cover ground compared to someone like me that can navigate the skies. I've been transporting other travelers like you to other regions in no time, and... are you interested? If so, then select the town you'd like yo head to.\r\n" + display)
            } else {
               cm.sendSimple("Hello there. How's the traveling so far? I've been transporting other travelers like you to other regions in no time, and... are you interested? If so, then select the town you'd like to head to.\r\n" + display)
            }
         } else if (status == 1) {
            slct = selection
            cm.sendYesNo("Will you move to #b" + menu[selection] + "#k now? If you have #b" + cost[selection] + " mesos#k, I'll take you there right now.")

         } else if (status == 2) {
            if (slct == 2) {
               if (cm.getMeso() < cost[2]) {
                  cm.sendNext("Are you sure you have enough mesos?")
                  cm.dispose()
               } else {
                  cm.gainMeso(-cost[2])
                  cm.warp(251000000, 0)
                  cm.dispose()
               }
            } else {
               if (cm.getMeso() < cost[slct]) {
                  cm.sendNext("Are you sure you have enough mesos?")
                  cm.dispose()
               } else {
                  if (cm.getPlayer().getMapId() == 251000000) {
                     cm.gainMeso(-cost[2])
                     cm.warp(250000100, 0)
                     cm.dispose()
                  } else {
                     EventManager em = cm.getEventManager("Hak")
                     if (!em.startInstance(cm.getPlayer())) {
                        cm.sendOk("Uh... We are currently taking requests from too many maplers right now... Please try again in a bit.")
                        cm.dispose()
                        return
                     }

                     cm.gainMeso(-cost[slct])
                     cm.dispose()
                  }
               }
            }
         }
      }
   }
}

NPC2090005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2090005(cm: cm))
   }
   return (NPC2090005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }