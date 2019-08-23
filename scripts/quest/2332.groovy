package quest


import scripting.quest.QuestActionManager

class Quest2332 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.hasItem(4032388) && !qm.isQuestStarted(2332)) {
         qm.forceStartQuest()
         qm.getPlayer().showHint("I must find Violetta!! (#bquest started#k)")
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2332 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2332(qm: qm))
   }
   return (Quest2332) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}