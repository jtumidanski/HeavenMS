package quest
import tools.I18nMessage

import client.MapleBuffStat
import client.MapleCharacter
import scripting.quest.QuestActionManager

class Quest3514 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.getPlayer().getMeso() >= 1000000) {
         if (qm.canHold(2022337, 1)) {
            qm.gainItem(2022337, (short) 1)
            qm.gainMeso(-1000000)

            //qm.sendOk(I18nMessage.from("3514_NICE_DOING_BUSINESS"))
            qm.startQuest(3514)
         } else {
            qm.sendOk(I18nMessage.from("3514_USE_SLOT_NEEDED"))
         }
      } else {
         qm.sendOk(I18nMessage.from("3514_NO_MONEY"))
      }

      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }

      if (status == 0) {
         if (!usedPotion(qm.getPlayer())) {
            if (qm.haveItem(2022337)) {
               qm.sendOk(I18nMessage.from("3514_ARE_YOU_SCARED"))
            } else {
               if (qm.canHold(2022337)) {
                  qm.gainItem(2022337, (short) 1)
                  qm.sendOk(I18nMessage.from("3514_LOST_IT"))
               } else {
                  qm.sendOk(I18nMessage.from("3514_LOST_IT_MAKE_ROOM"))
               }
            }

            qm.dispose()
         } else {
            qm.sendOk(I18nMessage.from("3514_POTION_WORKED"))
         }
      } else if (status == 1) {
         qm.gainExp(891500)
         qm.completeQuest(3514)
         qm.dispose()
      }
   }

   static def usedPotion(MapleCharacter ch) {
      return ch.getBuffSource(MapleBuffStat.HP_RECOVERY) == 2022337
   }
}

Quest3514 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3514(qm: qm))
   }
   return (Quest3514) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}