package quest


import scripting.quest.QuestActionManager

class Quest2197 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.sendNext("Oh, you already have monster book. Good luck on your journey~!")
      qm.forceCompleteQuest()
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.sendNext("Oh, you already have monster book. Good luck on your journey~!")
      qm.forceCompleteQuest()
      qm.dispose()
   }
}

Quest2197 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2197(qm: qm))
   }
   return (Quest2197) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}