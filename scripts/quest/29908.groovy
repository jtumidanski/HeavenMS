package quest


import scripting.quest.QuestActionManager

class Quest29908 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.getPlayer().getJob().getId() > 1000 && qm.getPlayer().getJob().getId() % 10 > 0 && qm.getPlayer().getJob().getId() < 2000) {
         qm.forceStartQuest()
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142068) && !qm.haveItem(1142068) && qm.getPlayer().getJob().getId() > 1000 && qm.getPlayer().getJob().getId() % 10 > 0 && qm.getPlayer().getJob().getId() < 2000) {
         qm.gainItem(1142068, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }
}

Quest29908 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29908(qm: qm))
   }
   return (Quest29908) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}