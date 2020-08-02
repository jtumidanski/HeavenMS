package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3953 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
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
            qm.sendSimple(I18nMessage.from("3953_DEO_IS_NOT_A_MONSTER"))
         } else if (status == 1) {
            qm.sendSimple(I18nMessage.from("3953_IS_THAT_SO"))
         } else if (status == 2) {
            qm.sendSimple(I18nMessage.from("3953_NOT_REALLY_DOING_WELL"))
         } else if (status == 3) {
            qm.sendSimple(I18nMessage.from("3953_THEY_HAVE_DEPARTED"))
         } else if (status == 4) {
            qm.gainItem(4011008, (short) -1)

            qm.sendNext(I18nMessage.from("3953_IN_GREAT_TROUBLE"))
            qm.gainExp(20000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest3953 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3953(qm: qm))
   }
   return (Quest3953) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}