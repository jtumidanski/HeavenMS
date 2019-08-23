package quest


import scripting.quest.QuestActionManager

class Quest29925 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142130) && !qm.haveItem(1142130, 1) && qm.getPlayer().getLevel() >= 30 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
         qm.gainItem(1142130, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.canHold(1142130) && !qm.haveItem(1142130, 1) && qm.getPlayer().getLevel() >= 30 && ((qm.getPlayer().getJob() / 100) | 0) == 21) {
         qm.gainItem(1142130, (short) 1)
         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }
      qm.dispose()
   }
}

Quest29925 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29925(qm: qm))
   }
   return (Quest29925) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}