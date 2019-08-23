package quest


import scripting.quest.QuestActionManager

class Quest2152 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.sendNext("That tree... I've heard of it before, I even studied its behavior! If I recall correctly, the #bStumpy#k comes alive when the soil deems infertile by some sort of magic, and those stumps who evolves under these conditions starts to drain these suspicious magical sources instead of water and minerals for living, which makes them very threathening to people and villages nearby.")
      qm.forceCompleteQuest()
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest2152 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2152(qm: qm))
   }
   return (Quest2152) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}