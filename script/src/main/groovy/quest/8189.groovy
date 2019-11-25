package quest

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
               qm.sendOk("Hey! I need #b10,000 mesos#k to do your pet's re-evolution!")
               qm.dispose()
               return
            }

            qm.sendYesNo("Alright then, let's do this again, shall we? As usual, it's going to be random, and I'm going to take away one of your Rock of Evolutions. \r\n\r #r#eReady?#n#k")
         } else if (status == 1) {
            qm.sendNextPrev("Then here we go...! #rHYAHH!#k")
         } else if (status == 2) {
            int petidx = -1
            int petItemid
            MaplePet pet
            for (int i = 0; i < 3; i++) {
               pet = qm.getPlayer().getPet(petidx)
               if (pet != null) {
                  int id = pet.getItemId()
                  if (id >= 5000029 && id <= 5000033) {
                     petItemid = 5000030
                     petidx = i
                     break
                  } else if (id >= 5000048 && id <= 5000053) {
                     // thanks Conrad for noticing Robo pets not being able to re-evolve
                     petItemid = 5000049
                     petidx = i
                     break
                  }
               }
            }

            if (petidx == -1) {
               qm.sendOk("Something wrong, try again.")
               qm.dispose()
               return
            }

            int pool = (petItemid == 5000030) ? 10 : 11
            while ({
               double rand = 1 + Math.floor(Math.random() * pool)
               int after = 0
               if (rand >= 1 && rand <= 3) {
                  after = petItemid
               } else if (rand >= 4 && rand <= 6) {
                  after = petItemid + 1
               } else if (rand >= 7 && rand <= 9) {
                  after = petItemid + 2
               } else if (rand == 10) {
                  after = petItemid + 3
               } else {
                  after = petItemid + 4
               }
               after == pet.id()
            }())
               continue

            /*if (name.equals(MapleItemInformationProvider.getInstance().getName(id))) {
name = MapleItemInformationProvider.getInstance().getName(after)
} */

            qm.gainMeso(-10000)
            qm.gainItem(5380000, (short) -1)
            qm.evolvePet((byte) petidx, after)

            qm.sendOk("Woo! It worked again! #rYou may find your new pet under your 'CASH' inventory.\r #kIt used to be a #b#i" + id + "##t" + id + "##k, and now it's \r a#b #i" + after + "##t" + after + "##k! \r\n Come back with 10,000 mesos and another Rock of Evolution if you don't like it!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + after + "# #t" + after + "#")
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