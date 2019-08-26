package quest


import scripting.quest.QuestActionManager

class Quest2338 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
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
            if (qm.haveItem(2430014, 1)) {
               qm.sendNext("It looks like you already have one #b#t2430014##k on your inventory.")
               status = 1
               return
            }

            qm.sendNext("You've used the #b#t2430014##k? Oh well, good thing I have a spare one right here.")
         } else if (status == 1) {
            if (!qm.canHold(2430014, 1)) {
               qm.sendNext("Please make a USE slot available to get it, alright?")
            } else {
               qm.gainItem(2430014, (short) 1)
               qm.forceCompleteQuest()
               qm.dispose()
            }
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2338 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2338(qm: qm))
   }
   return (Quest2338) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}