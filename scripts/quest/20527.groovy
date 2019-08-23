package quest

import client.MapleMount
import scripting.quest.QuestActionManager

class Quest20527 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            MapleMount mount = qm.getPlayer().getMount()

            if (mount != null && mount.getLevel() >= 3) {
               qm.sendNext("Alright, I'll get you started in how to train Mimio, the next step for Mimianas. When you're ready, talk to me again.")
               qm.forceCompleteQuest()
            } else {
               qm.sendNext("It looks like your Mimiana haven't reached #rlevel 3#k yet. Please train it a bit more before trying to advance it.")
            }

            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20527 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20527(qm: qm))
   }
   return (Quest20527) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}