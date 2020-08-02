package quest
import tools.I18nMessage

import client.MapleBuffStat
import client.MapleCharacter
import scripting.quest.QuestActionManager

class Quest3941 {
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
            if (!isTigunMorphed(qm.getPlayer())) {
               qm.sendNext(I18nMessage.from("3941_WHAT_IS_THIS"))
               status = 1
               return
            }

            qm.sendNext(I18nMessage.from("3941_WHAT_ARE_YOU_DOING_HERE"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("3941_WANTS_HER_SILK"))
            qm.forceStartQuest()
         } else if (status == 2) {
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
            if (!isTigunMorphed(qm.getPlayer())) {
               qm.sendNext(I18nMessage.from("3941_GET_OUT_OF_MY_SIGHTS"))
               qm.dispose()
               return
            }

            if (qm.canHold(4031571, 1)) {
               qm.gainItem(4031571)

               qm.sendNext(I18nMessage.from("3941_THERE_YOU_GO"))
               qm.forceCompleteQuest()
            } else {
               qm.sendNext(I18nMessage.from("3941_YOU_ARE_LACKING_SPACE"))
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }

   static def isTigunMorphed(MapleCharacter ch) {
      return ch.getBuffSource(MapleBuffStat.MORPH) == 2210005
   }
}

Quest3941 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3941(qm: qm))
   }
   return (Quest3941) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}