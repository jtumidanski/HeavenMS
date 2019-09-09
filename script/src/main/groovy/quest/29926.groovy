package quest


import scripting.quest.QuestActionManager

class Quest29926 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142131) && !qm.haveItem(1142131, 1) && qm.getPlayer().getLevel() >= 70 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
         qm.gainItem(1142131, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142131) && !qm.haveItem(1142131, 1) && qm.getPlayer().getLevel() >= 70 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
         qm.gainItem(1142131, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }
}

Quest29926 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29926(qm: qm))
   }
   return (Quest29926) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}