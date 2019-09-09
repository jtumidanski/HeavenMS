package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleMap

/*
	NPC Name: 		Growlie
	Map(s): 		
	Description: 	
*/


class NPC1012114 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int chosen = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 0) {
            status += ((chosen == 2) ? 1 : -1)
         } else {
            status++
         }

         if (status == 0) {
            if (cm.isEventLeader()) {
               cm.sendSimple("Growl! I am Growlie, always ready to protect this place. What brought you here?\r\n#b#L0# Please tell me what this place is all about.#l\r\n#L1# I have brought #t4001101#.#l\r\n#L2# I would like to leave this place.#l")
            } else {
               cm.sendSimple("Growl! I am Growlie, always ready to protect this place. What brought you here?\r\n#b#L0# Please tell me what this place is all about.#l\r\n#L2# I would like to leave this place.#l")
            }
         } else if (status == 1) {
            if (chosen == -1) {
               chosen = selection
            }
            if (chosen == 0) {
               cm.sendNext("This place can be best described as the prime spot where you can taste the delicious rice cakes made by Moon Bunny every full moon.")
            } else if (chosen == 1) {
               if (cm.haveItem(4001101, 10)) {
                  cm.sendNext("Oh... isn't this rice cake made by Moon Bunny? Please hand me the rice cake. Mmmm ... these seems delicious. Please come see me next time for more #b#t4001101##k. Have a safe trip home!")
               } else {
                  cm.sendOk("I advise you to check and make sure that you have indeed gathered up #b10 #t4001101#s#k.")
                  cm.dispose()
               }
            } else if (chosen == 2) {
               cm.sendYesNo("Are you sure you want to leave?")
            } else {
               cm.dispose()
            }
         } else if (status == 2) {
            if (chosen == 0) {
               cm.sendNextPrev("Gather up the primrose seeds from the primrose leaves all over this area, and plant the seeds at the footing near the crescent moon to see the primrose bloom. There are 6 types of primroses, and all of them require different footings. It is imperative that the footing fits the seed of the flower.")
            } else if (chosen == 1) {
               cm.gainItem(4001101, (short) -10)

               EventInstanceManager eim = cm.getEventInstance()
               clearStage(1, eim)

               MapleMap map = eim.getMapInstance(cm.getPlayer().getMapId())
               map.killAllMonstersNotFriendly()

               eim.clearPQ()
               cm.dispose()
            } else {
               if (mode == 1) {
                  cm.warp(910010300)
               } else {
                  cm.sendOk("You better collect some delicious rice cakes for me then, because time is running out, Growl!")
               }
               cm.dispose()
            }
         } else if (status == 3) {
            if (chosen == 0) {
               cm.sendNextPrev("When the flowers of primrose blooms, the full moon will rise, and that's when the Moon Bunnies will appear and start pounding the mill. Your task is to fight off the monsters to make sure that Moon Bunny can concentrate on making the best rice cake possible.")
            }
         } else if (status == 4) {
            if (chosen == 0) {
               cm.sendNextPrev("I would like for you and your party members to cooperate and get me 10 rice cakes. I strongly advise you to get me the rice cakes within the allotted time.")
            }
         } else {
            cm.dispose()
         }
      }
   }

   static def clearStage(int stage, EventInstanceManager eim) {
      eim.setProperty(stage + "stageclear", "true")
      eim.showClearEffect(true)
      eim.giveEventPlayersStageReward(stage)
   }
}

NPC1012114 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012114(cm: cm))
   }
   return (NPC1012114) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }