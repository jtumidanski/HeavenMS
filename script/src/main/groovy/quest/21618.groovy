package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21618 {
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
            qm.sendNext("Oh, this befriended wolf of yours... I sense some hidden powers hidden behind his furs, you see. Wat'cha say, master, if I awaken it's hidden power?", (byte) 9)
         } else if (status == 1) {
            qm.sendNextPrev("Wait, can you do that?", (byte) 3)
         } else if (status == 2) {
            qm.sendAcceptDecline("Astonished, huh? Does all that time frozen in the glacier hindered your senses as well? Why, of course! Tell me when you're ready!", (byte) 9)
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
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
            if (!qm.haveItemWithId(1902017, false)) {
               qm.sendNext(I18nMessage.from("21618_UNEQUIP_THE_WOLF"))
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("21618_STEP_ASIDE"))
         } else if (status == 1) {
            qm.forceCompleteQuest()
            qm.gainItem(1902017, (short) -1)
            qm.gainItem(1902018, (short) 1)
            qm.dispose()
         }
      }
   }
}

Quest21618 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21618(qm: qm))
   }
   return (Quest21618) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}