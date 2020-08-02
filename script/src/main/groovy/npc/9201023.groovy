package npc
import tools.I18nMessage

import client.MapleCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201023 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state
   int item
   int[] mats
   int[] matQty
   int cost
   int nanaLoc
   int[] mapIds = [100000000, 103000000, 102000000, 101000000, 200000000, 220000000]
   int[] questItems = [4000001, 4000037, 4000215, 4000026, 4000070, 4000128]
   int[] questExp = [2000, 5000, 10000, 17000, 22000, 30000]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

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
      int mapId = player.getMap().getId()

      for (int i = 0; i < mapIds.length; i++) {
         if (mapId == mapIds[i]) {
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

            cm.sendOk(I18nMessage.from("9201023_THANK_YOU"))
            return true
         } else {
            cm.sendOk(I18nMessage.from("9201023_NEED_ETC_SPACE"))
         }
      } else {
         cm.sendOk(I18nMessage.from("9201023_GATHER").with(questItems[nanaLoc]))
      }

      return false
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
               cm.sendOk(I18nMessage.from("9201023_HELLO"))
               cm.dispose()
               return
            }

            nanaLoc = getNanaLocation(cm.getPlayer())
            if (nanaLoc == -1) {
               cm.sendOk(I18nMessage.from("9201023_HELLO"))
               cm.dispose()
               return
            }

            if (!cm.haveItem(4031367 + nanaLoc, 1)) {
               if (cm.isQuestCompleted(100401 + nanaLoc)) {
                  state = 1
                  cm.sendAcceptDecline(I18nMessage.from("9201023_DID_YOU_LOSE").with(questItems[nanaLoc]))
               } else if (cm.isQuestStarted(100401 + nanaLoc)) {
                  if (processNanaQuest()) {
                     cm.gainExp(questExp[nanaLoc] * cm.getPlayer().getExpRate())
                     cm.completeQuest(100401 + nanaLoc)
                  }

                  cm.dispose()
               } else {
                  state = 0
                  cm.sendAcceptDecline(I18nMessage.from("9201023_SEARCHING_FOR"))
               }
            } else {
               cm.sendOk(I18nMessage.from("9201023_HEY_THERE"))
               cm.dispose()
            }
         } else if (status == 1) {
            if (state == 0) {
               cm.startQuest(100401 + nanaLoc)

               cm.sendOk(I18nMessage.from("9201023_COLLECT").with(questItems[nanaLoc]))
               cm.dispose()
            } else {
               processNanaQuest()
               cm.dispose()
            }
         }
      }
   }
}

NPC9201023 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201023(cm: cm))
   }
   return (NPC9201023) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }