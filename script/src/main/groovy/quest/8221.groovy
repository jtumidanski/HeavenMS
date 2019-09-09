package quest


import scripting.quest.QuestActionManager

class Quest8221 {
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
         qm.sendAcceptDecline("It's about time! We need to make you a way to travel safely to the summit of the Crimsonwood Valley, or else all we've been doing was for naught. You have to lay hands on the #b#t3992039##k. Are you ready to go?")
      } else if (status == 1) {
         qm.sendOk("Okay, I need you to have these items on hand first: #b10 #t4010006##k, #b4 #t4032005##k and #b1 #t4004000##k. Go!")
         qm.forceStartQuest()
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8221 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8221(qm: qm))
   }
   return (Quest8221) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}