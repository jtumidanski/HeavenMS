package quest


import scripting.quest.QuestActionManager

class Quest2151 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.sendNext("The tree has a strange carving that resembles a scary face.")
      qm.forceCompleteQuest()
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest2151 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2151(qm: qm))
   }
   return (Quest2151) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}