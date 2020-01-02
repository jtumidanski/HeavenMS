package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.life.MaplePlayerNPC

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1202010 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   boolean spawnPlayerNpc = false
   int spawnPlayerNpcFee = 7000000
   int jobType = 21

   def start() {
      if ((cm.getJobId() / 100).intValue() == jobType && cm.canSpawnPlayerNpc(GameConstants.getHallOfFameMapId(cm.getJob()))) {
         spawnPlayerNpc = true

         String sendStr = "You have walked a long way to reach the power, wisdom and courage you hold today, haven't you? What do you say about having right now #ra NPC on the Hall of Fame holding the current image of your character#k? Do you like it?"
         if (spawnPlayerNpcFee > 0) {
            sendStr += " I can do it for you, for the fee of #b " + cm.numberWithCommas(spawnPlayerNpcFee) + " mesos.#k"
         }

         cm.sendYesNo(sendStr)
      } else {
         cm.sendOk("Behold, the distinguished brave heroes of Rien! Those stout hearts are who have been protecting our people since long ago, our valiant comrades.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type != 1) {
         status -= 2
      }
      if (status == -1) {
         start()
      } else {
         if (spawnPlayerNpc) {
            if (mode > 0) {
               if (cm.getMeso() < spawnPlayerNpcFee) {
                  cm.sendOk("Sorry, you don't have enough mesos to purchase your place on the Hall of Fame.")
                  cm.dispose()
                  return
               }

               if (MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapId(cm.getJob()), cm.getPlayer())) {
                  cm.sendOk("There you go! Hope you will like it.")
                  cm.gainMeso(-spawnPlayerNpcFee)
               } else {
                  cm.sendOk("Sorry, the Hall of Fame is currently full...")
               }
            }

            cm.dispose()
         } else {
            // do nothing
         }
      }
   }
}

NPC1202010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1202010(cm: cm))
   }
   return (NPC1202010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }