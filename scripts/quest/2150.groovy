package quest


import scripting.quest.QuestActionManager

class Quest2150 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.sendNext("The tree has a scarf upon its branches, I tell you.")
      qm.forceCompleteQuest()
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest2150 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2150(qm: qm))
   }
   return (Quest2150) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}