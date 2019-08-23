package quest


import scripting.quest.QuestActionManager

class Quest3345 {
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
            if (qm.getQuestProgress(3345, 0) == 4) {
               qm.sendNext("So, you have succeeded. With this, Magatia's upfront demise has been averted, well done brave adventurer!")
               qm.forceCompleteQuest()

               qm.gainExp(20000)
            } else {
               qm.sendNext("Did you not seal the #rmagic circle beneath Magatia#k yet? It is a matter of great importance, please haste yourself.")
            }

            qm.dispose()
         }
      }
   }
}

Quest3345 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3345(qm: qm))
   }
   return (Quest3345) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}