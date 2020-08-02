package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest8221 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("8221_SEE_YOU_AROUND"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("8221_ABOUT_TIME"))
      } else if (status == 1) {
         qm.sendOk(I18nMessage.from("8221_I_NEED_THESE"))
         qm.forceStartQuest()
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8221 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8221(qm: qm))
   }
   return (Quest8221) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}