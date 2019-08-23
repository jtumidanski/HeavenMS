package quest


import scripting.quest.QuestActionManager

class Quest29906 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.getPlayer().getJob().getId() > 1000 && qm.getPlayer().getJob().getId() < 2000) {
         qm.forceStartQuest()
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142066) && !qm.hasItem(1142066) && (qm.getPlayer().getJob().getId() > 1000 && qm.getPlayer().getJob().getId() < 2000)) {
         qm.gainItem(1142066, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }
}

Quest29906 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29906(qm: qm))
   }
   return (Quest29906) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}