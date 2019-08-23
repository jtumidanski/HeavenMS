package quest


import scripting.quest.QuestActionManager

class Quest3527 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.startQuest()
      //qm.getPlayer().updateQuestInfo(3507, "1");
      qm.completeQuest()
      qm.sendOk("You have regained your memories, talk to #b#p2140001##k to get the pass.")
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest3527 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3527(qm: qm))
   }
   return (Quest3527) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}