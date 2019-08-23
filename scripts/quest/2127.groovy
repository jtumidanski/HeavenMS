package quest


import scripting.quest.QuestActionManager

class Quest2127 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.sendOk("I see you're ready for the task. Now, pay heed to the details of your mission...")
      qm.forceCompleteQuest()

      qm.dispose()
   }
}

Quest2127 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2127(qm: qm))
   }
   return (Quest2127) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}