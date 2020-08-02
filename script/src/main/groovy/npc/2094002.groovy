package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2094002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int level = 1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (cm.getPlayer().getMapId() == 925100700) {
         cm.warp(251010404, 0)
         cm.dispose()
         return
      }

      if (status == 1) {   // leaders cant withdraw
         cm.warp(251010404, 0)
         return
      }

      if (!cm.isEventLeader()) {
         cm.sendYesNo(I18nMessage.from("2094002_LEADER_MUST_SPEAK"))

      } else {
         EventInstanceManager eim = cm.getEventInstance()
         if (eim == null) {
            cm.warp(251010404, 0)
            cm.sendNext(I18nMessage.from("2094002_HOW"))

            cm.dispose()
            return
         }

         level = eim.getProperty("level").toInteger()

         switch (cm.getPlayer().getMapId()) {
            case 925100000:
               cm.sendNext(I18nMessage.from("2094002_DESTROY_ALL_MONSTERS"))

               cm.dispose()
               break
            case 925100100:
               String emp = eim.getProperty("stage2")
               if (emp == "0") {
                  if (cm.haveItem(4001120, 20)) {
                     cm.sendNext(I18nMessage.from("2094002_EXCELLENT"))

                     cm.gainItem(4001120, (short) -20)
                     cm.getMap().killAllMonsters()
                     eim.setProperty("stage2", "1")
                  } else {
                     cm.sendNext(I18nMessage.from("2094002_QUALIFY_AS_NOBLE_PIRATES"))

                  }
               } else if (emp == "1") {
                  if (cm.haveItem(4001121, 20)) {
                     cm.sendNext(I18nMessage.from("2094002_EXCELLENT_VETERAN"))

                     cm.gainItem(4001121, (short) -20)
                     cm.getMap().killAllMonsters()
                     eim.setProperty("stage2", "2")
                  } else {
                     cm.sendNext(I18nMessage.from("2094002_QUALIFY_AS_NOBLE_RISING_PIRATES"))

                  }
               } else if (emp == "2") {
                  if (cm.haveItem(4001122, 20)) {
                     cm.sendNext(I18nMessage.from("2094002_LET_US_GO"))

                     cm.gainItem(4001122, (short) -20)
                     cm.getMap().killAllMonsters()
                     eim.setProperty("stage2", "3")
                     eim.showClearEffect(cm.getMapId())
                  } else {
                     cm.sendNext(I18nMessage.from("2094002_QUALIFY_AS_NOBLE_VETERAN_PIRATES"))

                  }
               } else {
                  cm.sendNext(I18nMessage.from("2094002_NEXT_STAGE"))

               }
               cm.dispose()
               break
            case 925100200:
            case 925100300:
               cm.sendNext(I18nMessage.from("2094002_DESTROY_THE_GUARDS"))

               cm.dispose()
               break
            case 925100201:
               if (cm.getMap().getMonsters().size() == 0) {
                  cm.sendNext(I18nMessage.from("2094002_CHEST_HAS_APPEARED"))

                  if (eim.getProperty("stage2a") == "0") {
                     cm.getMap().setReactorState()
                     eim.setProperty("stage2a", "1")
                  }
               } else {
                  cm.sendNext(I18nMessage.from("2094002_LIBERATE_THEM"))
               }
               cm.dispose()
               break
            case 925100301:
               if (cm.getMap().getMonsters().size() == 0) {
                  cm.sendNext(I18nMessage.from("2094002_CHEST_HAS_APPEARED"))

                  if (eim.getProperty("stage3a") == "0") {
                     cm.getMap().setReactorState()
                     eim.setProperty("stage3a", "1")
                  }
               } else {
                  cm.sendNext(I18nMessage.from("2094002_LIBERATE_THEM"))
               }
               cm.dispose()
               break
            case 925100202:
            case 925100302:
               cm.sendNext(I18nMessage.from("2094002_KILL_THEM"))
               cm.dispose()
               break
            case 925100400:
               cm.sendNext(I18nMessage.from("2094002_SEAL_IT"))
               cm.dispose()
               break
            case 925100500:
               if (cm.getMap().getMonsters().size() == 0) {
                  cm.sendNext(I18nMessage.from("2094002_THANKS"))
               } else {
                  cm.sendNext(I18nMessage.from("2094002_DEFEAT_ALL_MONSTERS"))
               }
               cm.dispose()
               break
         }
      }
   }
}

NPC2094002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2094002(cm: cm))
   }
   return (NPC2094002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }