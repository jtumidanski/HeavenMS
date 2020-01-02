package quest


import scripting.quest.QuestActionManager

class Quest2219 {
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
            qm.sendNext("Did you know, they say someone from the sewers has been trying to #rdevelop a magic powder that let's one to grow#k, isn't that nice?")
         } else if (status == 1) {
            qm.forceCompleteQuest()
            qm.gainExp(7000)

            if (areAllSubQuestsDone() && qm.haveItem(4031894)) {
               qm.gainItem(4031894, (short) -1)
            }

            qm.dispose()
         }
      }
   }

   def areAllSubQuestsDone() {
      for (int i = 2216; i <= 2219; i++) {
         if (!qm.isQuestCompleted(i)) {
            return false
         }
      }

      return true
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2219 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2219(qm: qm))
   }
   return (Quest2219) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}