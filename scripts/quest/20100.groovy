package quest


import scripting.quest.QuestActionManager

class Quest20100 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         status++
      } else {
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendAcceptDecline("Ahhh, you're back. I can see that you're at level 10 now. It looks like you're flashing a glimmer of hope towards becoming a Knight. The basic training has now ended, and it's time for you to make the decision.")
      } else if (status == 1) {
         qm.sendOk("Now look to the left. The leaders of the Knights will be waiting for you. There will be 5 paths for you to choose from. All you need to do is choose one of them. All 5 of them will lead you to the path of a Knight, so... I suggest you pay attention to what each path offers, and select the one you'd most like to take.")
         qm.forceStartQuest()
         qm.forceCompleteQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20100 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20100(qm: qm))
   }
   return (Quest20100) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}