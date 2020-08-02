package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20408 {
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
            qm.sendNext(I18nMessage.from("20408_FIRST_OF_ALL"))

         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("20408_IF_NOTHING_ELSE"))

         } else if (status == 2) {
            qm.sendAcceptDecline(I18nMessage.from("20408_TO_CELEBRATE"))

         } else if (status == 3) {
            if (!qm.canHold(1142069, 1)) {
               qm.sendOk(I18nMessage.from("20408_MAKE_ROOM"))

               qm.dispose()
               return
            }

            qm.gainItem(1142069, (short) 1)
            if (qm.getJobId() % 10 == 1) {
               qm.changeJobById(qm.getJobId() + 1)
            }

            qm.forceStartQuest()
            qm.forceCompleteQuest()

            qm.sendOk(I18nMessage.from("20408_APPOINT_YOU"))

         } else if (status == 4) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20408 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20408(qm: qm))
   }
   return (Quest20408) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}