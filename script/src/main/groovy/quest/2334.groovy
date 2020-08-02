package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2334 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.forceStartQuest()
         qm.sendNext(I18nMessage.from("2334_THANK_YOU"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("2334_IT_IS_HUMILIATING"))
      } else if (status == 2) {
         qm.sendNextPrev("I see...\r\n#b(Wow, how pretty could she be?)", (byte) 2)
      } else if (status == 3) {
         qm.sendNextPrev("#b(What the--)", (byte) 2)
      } else if (status == 4) {
         qm.sendNextPrev("#b(Is that what's considered pretty in the world of mushrooms?!)", (byte) 2)
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("2334_I_AM_SO_SHY"))
      } else if (status == 6) {
         qm.forceStartQuest()
         qm.gainExp(1000)
         qm.forceCompleteQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2334 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2334(qm: qm))
   }
   return (Quest2334) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}