package quest


import scripting.quest.QuestActionManager

class Quest29907 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if ((qm.getPlayer().getJob().getId() > 1000 && qm.getPlayer().getJob().getId() < 2000) && qm.getPlayer().getJob().getId() % 100 == 10) {
         qm.forceStartQuest()
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142067) && !qm.haveItem(1142067) && qm.getPlayer().getJob().getId() > 1000 && qm.getPlayer().getJob().getId() % 100 > 0 && qm.getPlayer().getJob().getId() < 2000) {
         qm.gainItem(1142067, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }
}

Quest29907 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29907(qm: qm))
   }
   return (Quest29907) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}