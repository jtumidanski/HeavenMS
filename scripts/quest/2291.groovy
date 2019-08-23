package quest

import scripting.event.EventManager
import scripting.quest.QuestActionManager

class Quest2291 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

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
            if (!qm.haveItem(4032521, 10)) {
               qm.sendNext("Hey, you didn't get #b10 #t4032521##k yet, did you?")
               qm.dispose()
               return
            }

            qm.sendNext("You got the #b#i4032521##k with you, great. Let me show you the way.")
         } else if (status == 1) {
            EventManager em = qm.getEventManager("RockSpiritVIP")
            if (!em.startInstance(qm.getPlayer())) {
               qm.sendOk("Uh... It looks like the rooms ahead are a bit crowded right now. Please wait around here for a bit, ok?")
               qm.dispose()
               return
            }

            qm.gainItem(4032521, (short) -10)
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest2291 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2291(qm: qm))
   }
   return (Quest2291) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}