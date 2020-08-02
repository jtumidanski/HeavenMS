package quest
import tools.I18nMessage

import config.YamlConfig
import scripting.quest.QuestActionManager

class Quest21302 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            qm.sendNext(I18nMessage.from("21302_AT_LEAST_YOU_TRIED"))
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21302_GIVE_ME_THE_JADE"))
         //Giant Polearm
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21302_WORK_MY_MAGIC"))
      } else if (status == 2) {
         if (!qm.isQuestCompleted(21302)) {
            if (!qm.canHold(1142131)) {
               qm.sendOk(I18nMessage.from("21302_EQUIP_INVENTORY_IS_FULL"))
               qm.dispose()
               return
            }

            if (qm.haveItem(4032312, 1)) {
               qm.gainItem(4032312, (short) -1)
            }

            qm.gainItem(1142131, true)
            qm.changeJobById(2111)

            if (YamlConfig.config.server.USE_FULL_ARAN_SKILLSET) {
               qm.teachSkill(21110002, (byte) 0, (byte) 20, -1)   //full swing
            }

            qm.completeQuest()
         }

         qm.sendNext(I18nMessage.from("21302_KEEP_TRAINING"))
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest21302 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21302(qm: qm))
   }
   return (Quest21302) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}