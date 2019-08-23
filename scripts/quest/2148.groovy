package quest


import scripting.quest.QuestActionManager

class Quest2148 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.sendNext("Some bats seems to accompany this tree wherever it goes. Creepy...")
      qm.forceCompleteQuest()
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest2148 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2148(qm: qm))
   }
   return (Quest2148) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}