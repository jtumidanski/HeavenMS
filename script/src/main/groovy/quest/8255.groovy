package quest


import scripting.quest.QuestActionManager

class Quest8255 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.forceStartQuest()
      qm.forceCompleteQuest()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.forceCompleteQuest()
   }
}

Quest8255 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8255(qm: qm))
   }
   return (Quest8255) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}