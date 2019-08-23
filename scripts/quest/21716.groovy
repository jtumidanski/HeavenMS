package quest


import scripting.quest.QuestActionManager

class Quest21716 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 2) {
            qm.sendNext("What? I don't think there are any suspects besides that kid. Please think again.")
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext("What did #p1032112# say?", (byte) 8)
      } else if (status == 1) {
         qm.sendNextPrev("#b(You tell her what #p1032112# observed.)#k", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline("A kid with a puppet? That seems very suspicious. I am sure that kid is the reason the Green Mushrooms have suddenly turned violent.")
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.sendNext("How dare this kid wreak havoc in the South Forest. Who knows how long it will take to restore the forest... I'll have to devote most of my time cleaning up the mess.", (byte) 2)
      } else if (status == 4) {
         qm.sendPrev("#b(You were able to find out what caused the changes in the Green Mushrooms. You should report #p1002104# and deliver the information you've collected.)#k", (byte) 2)
      } else if (status == 5) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest21716 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21716(qm: qm))
   }
   return (Quest21716) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}