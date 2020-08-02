package quest
import tools.I18nMessage

import config.YamlConfig
import scripting.quest.QuestActionManager

class Quest21201 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.sendNext(I18nMessage.from("21201_AT_LEAST_YOU_TRIED"))
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.sendNext(I18nMessage.from("21201_AT_LEAST_YOU_TRIED"))
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext(I18nMessage.from("21201_FIRST_YOU_PROMISE"))
         } //Giant Polearm
         else if (status == 1) {
            qm.sendNextPrev("I did tell #p1203000# to make a pole arm for me if I could prove my worth.", (byte) 2)
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("21201_AFTER_ALL_THAT_BEGGING"))
         } else if (status == 3) {
            qm.sendNextPrev("Hey, I never begged for you.", (byte) 2)
         } else if (status == 4) {
            qm.sendNextPrev(I18nMessage.from("21201_YOU_GOT_ON_YOUR_KNEES"))
         } else if (status == 5) {
            qm.sendNextPrev("Maybe a little bit...", (byte) 2)
         } else if (status == 6) {
            qm.sendNextPrev(I18nMessage.from("21201_JUST_ALLERGIES"))
         } else if (status == 7) {
            qm.sendAcceptDecline(I18nMessage.from("21201_STILL_MY_MASTER"))
         } else if (status == 8) {
            if (!qm.isQuestCompleted(21201)) {
               if (!qm.canHold(1142130)) {
                  qm.sendOk(I18nMessage.from("21201_INVENTORY_IS_FULL"))
                  return
               }

               qm.gainItem(1142130, true)
               qm.changeJobById(2110)

               if (YamlConfig.config.server.USE_FULL_ARAN_SKILLSET) {
                  qm.teachSkill(21100000, (byte) 0, (byte) 20, -1)   //polearm mastery
                  qm.teachSkill(21100002, (byte) 0, (byte) 30, -1)   //final charge
                  qm.teachSkill(21100004, (byte) 0, (byte) 20, -1)   //combo smash
                  qm.teachSkill(21100005, (byte) 0, (byte) 20, -1)   //combo drain
               }

               qm.completeQuest()
            }

            qm.sendNext(I18nMessage.from("21201_I_CAN_RESTORE_A_FEW"))
         } else if (status == 9) {
            qm.dispose()
         }
      }
   }
}

Quest21201 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21201(qm: qm))
   }
   return (Quest21201) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}