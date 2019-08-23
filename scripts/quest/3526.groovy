package quest


import scripting.quest.QuestActionManager

class Quest3526 {
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

Quest3526 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3526(qm: qm))
   }
   return (Quest3526) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}