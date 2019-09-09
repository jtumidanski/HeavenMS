package npc

import client.MapleCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201026 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state
   int item
   int[] mats
   int[] matQty
   int cost
   int nanaLoc
   int[] mapids = [100000000, 103000000, 102000000, 101000000, 200000000, 220000000]
   int[] questItems = [4000001, 4000037, 4000215, 4000026, 4000070, 4000128]
   int[] questExp = [2000, 5000, 10000, 17000, 22000, 30000]

   static def hasProofOfLoves(MapleCharacter player) {
      int count = 0

      for (int i = 4031367; i <= 4031372; i++) {
         if (player.haveItem(i)) {
            count++
         }
      }

      return count >= 4
   }

   def getNanaLocation(MapleCharacter player) {
      int mapid = player.getMap().getId()

      for (int i = 0; i < mapids.length; i++) {
         if (mapid == mapids[i]) {
            return i
         }
      }

      return -1
   }

   def processNanaQuest() {
      if (cm.haveItem(questItems[nanaLoc], 50)) {
         if (cm.canHold(4031367 + nanaLoc, 1)) {
            cm.gainItem(questItems[nanaLoc], (short) -50)
            cm.gainItem(4031367 + nanaLoc, (short) 1)

            cm.sendOk("Kyaaaa~ Thank you a lot, here get the #b#t4031367##k.")
            return true
         } else {
            cm.sendOk("Please have a free ETC slot available to hold the token of love.")
         }
      } else {
         cm.sendOk("Please gather to me #b50 #t" + questItems[nanaLoc] + "##k.")
      }

      return false
   }

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (!cm.isQuestStarted(100400)) {
               cm.sendOk("Hello #b#h0##k, I'm #p9201026# the fairy of Love.")
               cm.dispose()
               return
            }

            nanaLoc = getNanaLocation(cm.getPlayer())
            if (nanaLoc == -1) {
               cm.sendOk("Hello #b#h0##k, I'm #p9201026# the fairy of Love.")
               cm.dispose()
               return
            }

            if (!cm.haveItem(4031367 + nanaLoc, 1)) {
               if (cm.isQuestCompleted(100401 + nanaLoc)) {
                  state = 1
                  cm.sendAcceptDecline("Did you lost the #k#t4031367##k I gave to you? Well, I can share another one with you, but you will need to redo the favor I asked last time, is that ok? I need you to bring me #r50 #t" + questItems[nanaLoc] + "#'s.#k")
               } else if (cm.isQuestStarted(100401 + nanaLoc)) {
                  if (processNanaQuest()) {
                     cm.gainExp(questExp[nanaLoc] * cm.getPlayer().getExpRate())
                     cm.completeQuest(100401 + nanaLoc)
                  }

                  cm.dispose()
               } else {
                  state = 0
                  cm.sendAcceptDecline("Are you searching for #k#t4031367#'s#k? I can share one with you, but you must do a favor for me, is that ok?")
               }
            } else {
               cm.sendOk("Hey there. Did you get the #t4031367# from the other Nana's already?")
               cm.dispose()
            }
         } else if (status == 1) {
            if (state == 0) {
               cm.startQuest(100401 + nanaLoc)

               cm.sendOk("I need you to collect #r50 #t" + questItems[nanaLoc] + "##k.")
               cm.dispose()
            } else {
               processNanaQuest()
               cm.dispose()
            }
         }
      }
   }
}

NPC9201026 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201026(cm: cm))
   }
   return (NPC9201026) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }