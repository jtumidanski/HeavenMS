package quest
import tools.I18nMessage

import scripting.event.EventManager
import scripting.quest.QuestActionManager
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

class Quest21401 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext(I18nMessage.from("21401_NEED_TO_THINK_ABOUT_THIS"))
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("21401_WHY_DO_I_LOOK_LIKE_THIS"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21401_SEED_OF_DARKNESS_PLANTED"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21401_I_WAS_MISTAKEN"))
      } else if (status == 3) {
         qm.sendAcceptDecline(I18nMessage.from("21401_STOP_ME_FROM_BECOMING_ENRAGED"))
      } else if (status == 4) {
         EventManager em = qm.getEventManager("MahaBattle")
         if (!em.startInstance(qm.getPlayer())) {
            qm.sendOk(I18nMessage.from("21401_SOMEONE_CURRENTLY_IN_MAP"))
         } else {
            qm.startQuest()
         }

         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext(I18nMessage.from("21401_NEED_TO_THINK_ABOU_TTHIS"))
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("21401_ONLY_YOUR_DUTY"))
      } else if (status == 1) {
         qm.sendYesNo(I18nMessage.from("21401_READY_TO_HANDLE_MORE"))
      } else if (status == 2) {
         if (!qm.isQuestCompleted(21401)) {
            if (!qm.canHold(1142132)) {
               qm.sendOk(I18nMessage.from("21401_EQUIP_INVENTORY_IS_FULL"))
               qm.dispose()
               return
            }
            if (!qm.canHold(2280003, 1)) {
               qm.sendOk(I18nMessage.from("21401_USE_INVENTORY_IS_FULL"))
               qm.dispose()
               return
            }

            qm.gainItem(1142132, true)
            qm.gainItem(2280003, (short) 1)
            qm.changeJobById(2112)

            qm.completeQuest()
         }
         qm.sendNext(I18nMessage.from("21401_SKILLS_HAVE_BEEN_RESTORED"))
      } else if (status == 3) {
         qm.dispose()
      }
   }

   static def spawnMob(x, y, int id, MapleMap map) {
      if (map.getMonsterById(id) != null) {
         return
      }

      MapleLifeFactory.getMonster(id).ifPresent({ mob -> map.spawnMonsterOnGroundBelow(mob, new Point(x, y)) })
   }
}

Quest21401 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21401(qm: qm))
   }
   return (Quest21401) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}