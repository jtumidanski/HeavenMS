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
               qm.sendOk("Hey! I need #b10,000 mesos#k to do your pet's evolution!")
               qm.dispose()
               return
            }

            qm.sendNext("#e#bHey, you did it!#n#k \r\n#rWow!#k Now I could complete my studies on your pet!")
         } else if (status == 1) {
            if (mode == 0) {
               qm.sendOk("I see... Come back when you wish to do it. I'm really excited to do this.")
               qm.dispose()
            } else {
               qm.sendNextPrev("Just saying, your new dragon's color is gonna be #e#rrandom#k#n! It's either gonna be #ggreen, #bblue, #rred, #dor very rarely#k, black. \r\n\r\n#fUI/UIWindow.img/QuestIcon/5/0# \r\n\r If you happen to not like your pet's new color, or if you ever wish to change your pet color again, #eyou can change it!#n Simply just #dbuy another Rock of Evolution, 10,000 mesos, #kand #dequip your new pet#k before talking to me again, but of course, I cannot return your pet as a baby dragon, only to another adult dragon.")
            }
         } else if (status == 2) {
            qm.sendYesNo("Now let me try to evolve your pet. You ready? Wanna see your cute baby dragon turn into either a matured dark black, blue, calm green, or fiery red adult dragon? It'll still have the same closeness, level, name, fullness, hunger, and equipment in case you're worried. \r\n\r #b#eDo you wish to continue or do you have some last-minute things to do first?#k#n")
         } else if (status == 3) {
            qm.sendNextPrev("Alright, here we go...! #rHYAHH!#k")
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
               qm.sendOk("Something wrong. Try again.")
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

            qm.sendOk("#bSWEET! IT WORKED!#k Your dragon has grown beautifully! #rYou may find your new pet under your 'CASH' inventory.\r #kIt used to be a #b #i5000029##t5000029##k, and now it's \r a #b#i" + after + "##t" + after + "##k!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + after + "# #t" + after + "#")
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