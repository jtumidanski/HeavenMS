package quest

import client.inventory.MaplePet
import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest8185 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            if (qm.getMeso() < 10000) {
               qm.sendOk(I18nMessage.from("8185_I_NEED_MESOS"))
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("8185_YOU_DID_IT"))
         } else if (status == 1) {
            if (mode == 0) {
               qm.sendOk(I18nMessage.from("8185_I_SEE"))
               qm.dispose()
            } else {
               qm.sendNextPrev(I18nMessage.from("8185_JUST_SAYING"))
            }
         } else if (status == 2) {
            qm.sendYesNo(I18nMessage.from("8185_LET_ME_TRY"))
         } else if (status == 3) {
            qm.sendNextPrev(I18nMessage.from("8185_HERE_WE_GO"))
         } else if (status == 4) {
            int rand = 1 + Math.floor(Math.random() * 10).intValue()

            int i
            for (i = 0; i < 3; i++) {
               if (qm.getPlayer().getPet(i) != null && qm.getPlayer().getPet(i).id() == 5000029) {
//                  MaplePet pet = qm.getPlayer().getPet(i)
                  break
               }
            }
            if (i == 3) {
               MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PET_COULD_NOT_BE_EVOLVED"))
               qm.dispose()
               return
            }


            int after
            if (rand >= 1 && rand <= 3) {
               after = 5000030
            } else if (rand >= 4 && rand <= 6) {
               after = 5000031
            } else if (rand >= 7 && rand <= 9) {
               after = 5000032
            } else if (rand == 10) {
               after = 5000033
            } else {
               qm.sendOk(I18nMessage.from("8185_SOMETHING_WRONG"))
               qm.dispose()
               return
            }

            /* if (name.equals(MapleItemInformationProvider.getInstance().getName(id))) {
   name = MapleItemInformationProvider.getInstance().getName(after);
} */

            //qm.unequipPet(qm.getClient());
            qm.gainItem(5380000, (short) -1)
            qm.gainMeso(-10000)
            qm.evolvePet((byte) i, after)

            //SpawnPetHandler.evolve(qm.getPlayer().getClient(), 5000029, after);

            qm.sendOk(I18nMessage.from("8185_SWEET").with(after, after, after, after))
         } else if (status == 5) {
            qm.dispose()
         }
      }
   }
}

Quest8185 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8185(qm: qm))
   }
   return (Quest8185) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}