package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21301 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext(I18nMessage.from("21301_NEED_TO_THINK_ABOUT_THIS"))
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("21301_DID_YOU_MANAGE"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21301_DID_YOU_FORGET"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21301_REMAIN_CALM"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("21301_START_A_NEW"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("21301_DEFINITELY_LOST"))
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("21301_NOOO"))
      } else if (status == 6) {
         qm.completeQuest()
         qm.sendNextPrev("#b(Maha is beginning to really get hysterical. I better leave right this minute. Maybe Lilin can do something about it.)", (byte) 2)
      } else if (status == 7) {
         qm.dispose()
      }
   }
}

Quest21301 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21301(qm: qm))
   }
   return (Quest21301) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}