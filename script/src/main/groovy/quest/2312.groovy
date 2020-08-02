package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2312 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2312_COME_WHEN_YOU_ARE_READY"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2312_NEED_YOUR_HELP"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("2312_KEEP_MOVING_FORWARD"))
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk(I18nMessage.from("2312_TEACH_THEM_A_LESSON"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(11500)
         qm.gainItem(4000499, (short) -50)
         qm.sendOk(I18nMessage.from("2312_AMAZING"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2312 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2312(qm: qm))
   }
   return (Quest2312) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}