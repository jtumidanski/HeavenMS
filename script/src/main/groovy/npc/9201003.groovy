package npc
import tools.I18nMessage

import client.MapleCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int numberOfLoves = 0
   int state = 0


   static def hasProofOfLoves(MapleCharacter player) {
      int count = 0

      for (int i = 4031367; i <= 4031372; i++) {
         if (player.haveItem(i)) {
            count++
         }
      }

      return count >= 4
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
               cm.sendOk(I18nMessage.from("9201003_HELLO"))
               cm.dispose()
            } else {
               if (cm.getQuestProgressInt(100400, 1) == 0) {
                  cm.sendNext("Mom, dad, I have a request to do to both of you... I wanna know more about the path you've already been walking since always, the path of loving and caring for someone dear to me.", (byte) 2)
               } else {
                  if (!hasProofOfLoves(cm.getPlayer())) {
                     cm.sendOk(I18nMessage.from("9201003_PLEASE_BRING"))
                     cm.dispose()
                  } else {
                     cm.sendNext(I18nMessage.from("9201003_MADE_US_PROUD"))
                     state = 1
                  }
               }
            }
         } else if (status == 1) {
            if (state == 0) {
               cm.sendNextPrev(I18nMessage.from("9201003_MY_DEAR"))
            } else {
               cm.sendOk("Mom... Dad... Thanks a lot for your tender support!!!", (byte) 2)
               cm.completeQuest(100400)
               cm.gainExp(20000 * cm.getPlayer().getExpRate())
               for (int i = 4031367; i <= 4031372; i++) {
                  cm.removeAll(i)
               }

               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("9201003_COLLECT_AND_BRING"))
         } else if (status == 3) {
            cm.setQuestProgress(100400, 1, 1)
            cm.dispose()
         }
      }
   }
}

NPC9201003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201003(cm: cm))
   }
   return (NPC9201003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }