package quest

import client.inventory.MaplePet
import scripting.quest.QuestActionManager

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
            qm.sendOk("Hey! I need #b10,000 mesos#k to do your pet's evolution!")
            qm.dispose()
            return
         }
         qm.sendNext("Great job on finding your evolution materials. I will now give you a robot.")
      } else if (status == 1) {
         if (qm.isQuestCompleted(4659)) {
            qm.getPlayer().dropMessage(1, "how did this get here?")
         } else if (qm.canHold(5000048)) {
            MaplePet pet = null
            int after
            int i

            for (i = 0; i < 3; i++) {
               if (qm.getPlayer().getPet(i) != null && qm.getPlayer().getPet(i).getItemId() == 5000048) {
                  pet = qm.getPlayer().getPet(i)
                  break
               }
            }
            if (i == 3) {
               qm.getPlayer().message("Pet could not be evolved.")
               qm.dispose()
               return
            }

            int closeness = pet.getCloseness()
            if (closeness < 1642) {
               qm.sendOk("It looks like your pet is not grown enough to be evolved yet. Train it a bit more, util it reaches #blevel 15#k.")
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
               qm.sendOk("Something wrong. Try again.")
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
         } else {
            qm.getPlayer().dropMessage(1, "Your inventory is full")
         }
         qm.dispose()
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