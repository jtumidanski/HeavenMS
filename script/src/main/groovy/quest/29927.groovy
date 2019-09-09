package quest


import scripting.quest.QuestActionManager

class Quest29927 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142132) && !qm.haveItem(1142132, 1) && qm.getPlayer().getLevel() >= 120 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
         qm.gainItem(1142132, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142132) && !qm.haveItem(1142132, 1) && qm.getPlayer().getLevel() >= 120 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
         qm.gainItem(1142132, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }
}

Quest29927 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29927(qm: qm))
   }
   return (Quest29927) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}