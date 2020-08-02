package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22001 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22001_FEED"))
      } else if (status == 1) {
         qm.sendNextPrev("#bWhat? That's #p1013101#'s job!", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("22001_LITTLE_BRAT"))
      } else if (status == 3) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("22001_STOP_BEING_LAZY"))
            qm.dispose()
         } else {//accept
            qm.gainItem(4032447, true)
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("22001_HURRY_UP"))
         }
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("22001_FEED_AND_COME_BACK"))
      } else if (status == 5) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22001 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22001(qm: qm))
   }
   return (Quest22001) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}