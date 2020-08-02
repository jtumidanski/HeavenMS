package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21703 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 6) {
            qm.sendNext(I18nMessage.from("21703_MOVE_ON_TO_BIGGER_AND_BETTER"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21703_BEGINNING_TO_TAKE_SHAPE"))
      } else if (status == 1) {
         qm.sendNextPrev("#b(You didn't even train that long with him... Why is he crying?)#k", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21703_FINAL_STAGE"))
      } else if (status == 3) {
         qm.sendNextPrev('Well, a little bit...', (byte) 2)
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("21703_NATURAL_WARRIORS"))
      } else if (status == 5) {
         qm.sendNextPrev("#b(Is that really true?)#k", (byte) 2)
      } else if (status == 6) {
         qm.sendAcceptDecline(I18nMessage.from("21703_SHOW_ME_WHAT_YOU_ARE_MADE_OF"))
      } else if (status == 7) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("21703_GO_AND_TAKE_ON"))
      } else if (status == 8) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 2) {
            qm.sendNext(I18nMessage.from("21703_YOU_RELUCTANT_TO_LEAVE"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21703_I_KNEW_YOU_HAD_IT_IN_YOU"))
      } else if (status == 1) {
         qm.sendNextPrev("#b(Is he pulling your leg?)#k'", (byte) 2)
      } else if (status == 2) {
         qm.sendYesNo(I18nMessage.from("21703_NOTHING_MORE_TO_TEACH"))
      } else if (status == 3) {
         if (qm.isQuestStarted(21703)) {
            qm.forceCompleteQuest()
            qm.teachSkill(21000000, (byte) qm.getPlayer().getSkillLevel(21000000), (byte) 10, -1)
            // Combo Ability Skill
            qm.gainExp(2800)
         }
         qm.sendNext("(You remembered the #bCombo Ability#k skill! You were skeptical of the training at first, since the old man suffers from Alzheimer's and all, but boy, was it effective!)", (byte) 2)
      } else if (status == 4) {
         qm.sendPrev(I18nMessage.from("21703_REPORT_BACK"))
      } else if (status == 5) {
         qm.dispose()
      }
   }
}

Quest21703 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21703(qm: qm))
   }
   return (Quest21703) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}