package quest


import scripting.quest.QuestActionManager

class Quest20600 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++

      if (status == 0) {
         qm.sendAcceptDecline("#h0#. Have you been slacking off on training since reaching Level 100? We all know how powerful you are, but the training is not complete. Take a look at these Knight Commanders. They train day and night, preparing themselves for the possible encounter with the Black Magician.")
      } else if (status == 1) {
         if (mode == 1) {
            qm.forceStartQuest()
         }
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest20600 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20600(qm: qm))
   }
   return (Quest20600) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}