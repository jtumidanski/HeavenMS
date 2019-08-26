package quest


import scripting.quest.QuestActionManager

class Quest20610 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++

      if (status == 0) {
         qm.sendAcceptDecline("Have you been mastering your skills? I am sure you've mastered all your skills, which means... it's time for you to learn a #bnew skill#k, right?")
      } else if (status == 1) {
         if (mode == 0) {
            qm.sendOk("Well, what you're doing right now doesn't make you look like someone that's humble. You just look complacent by doing that, and that's never a good thing.")
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest20610 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20610(qm: qm))
   }
   return (Quest20610) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}