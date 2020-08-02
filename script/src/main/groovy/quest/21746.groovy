package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager
import server.maps.MapleMap

class Quest21746 {
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
            qm.sendNext(I18nMessage.from("21746_PASS_MY_TEST"))
         } else {
            MapleMap map = qm.getWarpMap(925040001)
            if (map.countPlayers() == 0) {
               map.resetPQ(1)

               qm.warp(925040001, 0)
               qm.forceStartQuest()
            } else {
               qm.sendOk(I18nMessage.from("21746_SOMEONE_ALREADY_ATTEMPTING"))
            }


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
            qm.sendNext(I18nMessage.from("21746_LET_ME_POUR_IT"))
         } else if (status == 1) {
            qm.gainItem(4032342, (short) -8)
            qm.gainItem(4220151, (short) -1)
            qm.gainExp(10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest21746 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21746(qm: qm))
   }
   return (Quest21746) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}