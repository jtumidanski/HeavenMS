package quest


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
            int pet
            if (qm.getPlayer().getPet(0).id() >= 5000029 && qm.getPlayer().getPet(0).id() <= 5000033) {
               pet = 0
            } else if (qm.getPlayer().getPet(1).id() >= 5000029 && qm.getPlayer().getPet(1).id() <= 5000033) {
               pet = 1
            } else if (qm.getPlayer().getPet(2).id() >= 5000029 && qm.getPlayer().getPet(2).id() <= 5000033) {
               pet = 2
            } else {
               qm.sendOk("Something wrong, try again.")
               qm.dispose()
               return
            }
            int id = qm.getPlayer().getPet(pet).id()
            if (id < 5000029 || id > 5000033) {
               qm.sendOk("Something wrong, try again.")
               qm.dispose()
               return
            }
            int rand = 1 + Math.floor(Math.random() * 10).intValue()
            int after = 0
            if (rand >= 1 && rand <= 3) {
               after = 5000030
            } else if (rand >= 4 && rand <= 6) {
               after = 5000031
            } else if (rand >= 7 && rand <= 9) {
               after = 5000032
            } else if (rand == 10) {
               after = 5000033
            } else {
               qm.sendOk("Something wrong. Try again.")
               qm.dispose()
               return
            }

            /*if (name.equals(MapleItemInformationProvider.getInstance().getName(id))) {
name = MapleItemInformationProvider.getInstance().getName(after);
} */

            qm.gainMeso(-10000)
            qm.gainItem(5380000, (short) -1)
            qm.evolvePet((byte) pet, after)

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