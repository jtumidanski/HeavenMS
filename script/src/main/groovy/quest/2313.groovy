package quest


import scripting.quest.QuestActionManager

class Quest2313 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("There's not much time. Please hurry.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("I have told our #bMinister of Home Affairs#k of your abilities. Please go pay a visit to him immediately.")
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk("Save our kingdom! We believe in you!")
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.forceCompleteQuest()
         qm.gainExp(4000)
         qm.dispose()
      }
   }
}

Quest2313 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2313(qm: qm))
   }
   return (Quest2313) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}