package quest
import tools.I18nMessage

import client.inventory.MaplePet
import scripting.quest.QuestActionManager

class Quest8189 {
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
               qm.sendOk(I18nMessage.from("8189_I_NEED_MESOS"))
               qm.dispose()
               return
            }

            qm.sendYesNo(I18nMessage.from("8189_ALRIGHT_THEN"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("8189_HERE_WE_GO"))
         } else if (status == 2) {
            int petId = -1
            int petItemId
            MaplePet pet
            int id
            for (int i = 0; i < 3; i++) {
               pet = qm.getPlayer().getPet(petId)
               if (pet != null) {
                  id = pet.id()
                  if (id >= 5000029 && id <= 5000033) {
                     petItemId = 5000030
                     petId = i
                     break
                  } else if (id >= 5000048 && id <= 5000053) {
                     petItemId = 5000049
                     petId = i
                     break
                  }
               }
            }

            if (petId == -1) {
               qm.sendOk(I18nMessage.from("8189_SOMETHING_WRONG"))
               qm.dispose()
               return
            }

            int pool = (petItemId == 5000030) ? 10 : 11
            int after = 0
            while ({
               double rand = 1 + Math.floor(Math.random() * pool)
               if (rand >= 1 && rand <= 3) {
                  after = petItemId
               } else if (rand >= 4 && rand <= 6) {
                  after = petItemId + 1
               } else if (rand >= 7 && rand <= 9) {
                  after = petItemId + 2
               } else if (rand == 10) {
                  after = petItemId + 3
               } else {
                  after = petItemId + 4
               }
               after == pet.id()
            }())
               continue

            /*if (name.equals(MapleItemInformationProvider.getInstance().getName(id))) {
name = MapleItemInformationProvider.getInstance().getName(after)
} */

            qm.gainMeso(-10000)
            qm.gainItem(5380000, (short) -1)
            qm.evolvePet((byte) petId, after)

            qm.sendOk(I18nMessage.from("8189_IT_WORKED").with(id, id, after, after, after, after))
         } else if (status == 3) {
            qm.dispose()
         }
      }
   }
}

Quest8189 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8189(qm: qm))
   }
   return (Quest8189) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}