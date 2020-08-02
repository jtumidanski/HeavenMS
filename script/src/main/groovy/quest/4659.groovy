package quest

import client.inventory.MaplePet
import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest4659 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         if (qm.getMeso() < 10000) {
            qm.sendOk(I18nMessage.from("4659_I_NEED_MESOS"))
            qm.dispose()
            return
         }
         qm.sendNext(I18nMessage.from("4659_GREAT_JOB"))
      } else if (status == 1) {
         if (qm.isQuestCompleted(4659)) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, "how did this get here?")
            qm.dispose()
         } else if (qm.canHold(5000048)) {
            MaplePet pet = null
            int after
            int i

            for (i = 0; i < 3; i++) {
               if (qm.getPlayer().getPet(i) != null && qm.getPlayer().getPet(i).id() == 5000048) {
                  pet = qm.getPlayer().getPet(i)
                  break
               }
            }
            if (i == 3) {
               MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PET_COULD_NOT_BE_EVOLVED"))
               qm.dispose()
               return
            }

            int closeness = pet.closeness()
            if (closeness < 1642) {
               qm.sendOk(I18nMessage.from("4659_NOT_GROWN_ENOUGH"))
               qm.dispose()
               return
            }

//            byte level = pet.getLevel()
//            int fullness = pet.getFullness()
//            String name = pet.getName()

            int rand = 1 + Math.floor(Math.random() * 9).intValue()

            if (rand >= 1 && rand <= 2) {
               after = 5000049
            } else if (rand >= 3 && rand <= 4) {
               after = 5000050
            } else if (rand >= 5 && rand <= 6) {
               after = 5000051
            } else if (rand >= 7 && rand <= 8) {
               after = 5000052
            } else if (rand == 9) {
               after = 5000053
            } else {
               qm.sendOk(I18nMessage.from("4659_SOMETHING_IS_WRONG"))
               qm.dispose()
               return
            }

            //qm.gainItem(5000048 + rand);
            qm.gainItem(5380000, (short) -1)
            qm.gainMeso(-10000)

            qm.evolvePet((byte) i, after)
//            var petId = MaplePet.createPet(rand + 5000049, level, closeness, fullness);
//            if (petId == -1) return;
//            MapleInventoryManipulator.addById(qm.getClient(), rand+5000049, 1, "", petId);
            qm.dispose()
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
            qm.dispose()
         }
      }
   }
}

Quest4659 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest4659(qm: qm))
   }
   return (Quest4659) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}