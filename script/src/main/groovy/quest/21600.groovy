package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21600 {
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
            qm.sendNext(I18nMessage.from("21600_IF_YOU_ASK_ME"))
         } else if (status == 1) {
            qm.sendAcceptDecline(I18nMessage.from("21600_FIRST_MAKE_YOUR_WAY"))
         } else if (status == 2) {
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("21600_THE_ONE_YOU_MUST_MEET"))
         } else if (status == 3) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21600 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21600(qm: qm))
   }
   return (Quest21600) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}