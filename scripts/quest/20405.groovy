package quest


import scripting.quest.QuestActionManager

class Quest20405 {
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
            qm.sendNext("There's a note on the wall: 'The source of the curse still goes missing, but a strange device, that I suppose has been used by #rthem#k was found here.'", (byte) 3)
         } else if (status == 1) {
            qm.sendNextPrev("'The machine was sent to #rEreve#k for avaliation, I'll now set out to continue my mission. Let the Empress bless me on my journey.'", (byte) 3)
         } else if (status == 2) {
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20405 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20405(qm: qm))
   }
   return (Quest20405) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}