package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21741 {
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
            qm.sendNext(I18nMessage.from("21741_HAVE_YOU_BEEN_ADVANCING"))
         } else if (status == 1) {
            qm.sendAcceptDecline(I18nMessage.from("21741_GO_FIND_OUT_WHY"))
         } else if (status == 2) {
            qm.sendNext(I18nMessage.from("21741_REMAIN_PATIENT"))
         } else if (status == 3) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21741 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21741(qm: qm))
   }
   return (Quest21741) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}