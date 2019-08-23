package quest


import scripting.quest.QuestActionManager

class Quest3507 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.isQuestCompleted(3523) || qm.isQuestCompleted(3524) || qm.isQuestCompleted(3525) || qm.isQuestCompleted(3526) || qm.isQuestCompleted(3527) || qm.isQuestCompleted(3529) || qm.isQuestCompleted(3539)) {
         qm.completeQuest()
         qm.sendOk("You are now filled with all of your memories again.. You are now allowed to go to #m270020000#.")
      } else {
         qm.sendOk("You have not yet checked with your first teacher about your memories?")
      }

      qm.dispose()
   }
}

Quest3507 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3507(qm: qm))
   }
   return (Quest3507) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}