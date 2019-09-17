package quest


import scripting.quest.QuestActionManager

class Quest8231 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Okay, then. See you around.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         String target = "are Leprechauns"
         qm.sendAcceptDecline("Hey, traveler! I need your help. A new threat has appeared to the citizens of the New Leaf City. I'm currently recruiting anyone, and this time's target #r" + target + "#k. Are you in?")
      } else if (status == 1) {
         String reqs = "#r30 #t4032031##k"
         qm.sendOk("Very well. Get me #r" + reqs + "#k, asap. The NLC is counting on you.")
         qm.forceStartQuest()
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8231 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8231(qm: qm))
   }
   return (Quest8231) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}