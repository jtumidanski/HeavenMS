package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest8237 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("8237_SEE_YOU_AROUND"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         String target = "is the Bigfoot"
         qm.sendAcceptDecline(I18nMessage.from("8237_NEED_YOUR_HELP").with(target))
      } else if (status == 1) {
         String reqs = "#r1 #t4032013##k"
         qm.sendOk(I18nMessage.from("8237_VERY_WELL").with(reqs))
         qm.forceStartQuest()
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8237 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8237(qm: qm))
   }
   return (Quest8237) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}