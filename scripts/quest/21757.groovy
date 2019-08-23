package quest


import scripting.quest.QuestActionManager

class Quest21757 {
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
            qm.sendNext("Oh, a letter for the #rempress#k? From the #bheroes#k?!")
         } else {
            qm.gainExp(1000)
            qm.gainItem(4032330, (short) -1)
            qm.forceCompleteQuest()

            qm.dispose()
         }
      }
   }
}

Quest21757 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21757(qm: qm))
   }
   return (Quest21757) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}