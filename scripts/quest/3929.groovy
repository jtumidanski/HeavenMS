package quest


import scripting.quest.QuestActionManager

class Quest3929 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            int c = 0

            for (int i = 0; i < 4; i++ ) {
               if (qm.getQuestProgress(3929, i) == 1) {
                  c++
               }
            }

            if (c == 4) {
               qm.sendNext("You delivered all the food, good.")
               qm.gainExp(2000)
               qm.forceCompleteQuest()
            } else {
               int missed = (4 - qm.getItemQuantity(4031580)) - c
               if (missed > 0) {
                  if (qm.canHold(4031580, missed)) {
                     qm.gainItem(4031580, (short) missed)
                     qm.sendNext("Hey, what are you trying to pull on? To pass my test you must deliver all the foods to the Residential areas.")
                  } else {
                     qm.sendNext("You don't completed the task, neither has slots available on the inventory to get the food. Free a slot on your ETC please.")
                  }
               } else {
                  qm.sendNext("Hey, what are you trying to pull on? To pass my test you must to deliver all the foods to the Residential areas.")
               }
            }

            qm.dispose()
         }
      }
   }
}

Quest3929 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3929(qm: qm))
   }
   return (Quest3929) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}