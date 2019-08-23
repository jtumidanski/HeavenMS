package quest


import scripting.quest.QuestActionManager

class Quest3114 {
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
            if (qm.getQuestProgress(3114, 7777) != -1) {
               if (!qm.haveItem(4161036, 1)) {
                  if (qm.canHold(4161036, 1)) {
                     qm.gainItem(4161036, (short) 1)
                     qm.sendNext("Seems you lost a book with the notes to Little Star. Here is another one. Please play it for me.", (byte) 9)
                  } else {
                     qm.sendNext("Seems you lost a book with the notes to Little Star, but you don't have an ETC available. Please free some room.", (byte) 9)
                  }
               } else {
                  qm.sendNext(".....", (byte) 9)
               }

               qm.dispose()
               return
            }

            qm.sendNext("(Eliza seems to be in deep sleep.)", (byte) 3)
         } else if (status == 1) {
            qm.gainFame(20)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest3114 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3114(qm: qm))
   }
   return (Quest3114) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}