package quest

import client.MapleBuffStat
import client.MapleCharacter
import scripting.quest.QuestActionManager

class Quest3314 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   static def isPillUsed(MapleCharacter ch) {
      return ch.getBuffSource(MapleBuffStat.HPREC) == 2022198
   }

   def end(Byte mode, Byte type, Integer selection) {
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
            if (isPillUsed(qm.getPlayer())) {
               if (qm.canHoldAll([2050004, 2022224], [10, 20])) {
                  qm.sendNext("You did took my experiments. Hmm, so THAT is the result of it, hehehehe... Ok, take that as compensation will you? And oh, you can #rspew that#k right away (#bright-click on the pill icon at the top-right corner of the screen#k), no worries.")

                  qm.gainExp(12500)
                  qm.gainItem(2050004, (short) 10)

                  int i = Math.floor(Math.random() * 5).intValue()
                  qm.gainItem(2022224 + i, (short) 10)

                  qm.forceCompleteQuest()
               } else {
                  qm.sendNext("Huh, your inventory is full. Free some spaces on your USE first.")
               }
            } else {
               qm.sendNext("You seem pretty normal, don't you? I can't detect any possible effect from my experiment on you. Go take the pill I asked you to take and show me the effects, will you?")
            }
         } else {
            qm.dispose()
         }
      }
   }
}

Quest3314 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3314(qm: qm))
   }
   return (Quest3314) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}