package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest20526 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (status >= 2 && mode == 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("20526_YOU_LOST_YOUR_MIMIANA"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("20526_CAN_BE_YOUR_FRIEND"))
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("20526_HERES_AN_EGG"))
      } else if (status == 3) {
         if (!qm.haveItem(4220137) && !qm.canHold(4220137)) {
            qm.sendOk(I18nMessage.from("20526_MAKE_ETC_ROOM"))
            qm.dispose()
            return
         }

         qm.forceStartQuest()

         if (!qm.haveItem(4220137)) {
            qm.gainItem(4220137)
         }
         qm.sendOk(I18nMessage.from("20526_SHARE_YOUR_EXPERIENCES"))
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode != 1) {
         qm.dispose()
         return
      }

      status++
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20526_HOWS_THE_EGG"))
      } else if (status == 1) {   //pretty sure there would need to have an egg EXP condition... Whatever.
         if (!qm.haveItem(4220137)) {
            qm.sendOk(I18nMessage.from("20526_YOU_LOST_YOUR_EGG"))
            qm.dispose()
            return
         }
         if (!qm.canHold(1902005)) {
            qm.sendOk(I18nMessage.from("20526_MAKE_EQUIP_SPACE"))
            qm.dispose()
            return
         }

         qm.forceCompleteQuest()
         qm.gainItem(1902005, (short) 1)
         qm.gainItem(4220137, (short) -1)
         qm.gainMeso(-10000000)
         qm.sendOk(I18nMessage.from("20526_TAKE_GOOD_CARE"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest20526 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20526(qm: qm))
   }
   return (Quest20526) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}