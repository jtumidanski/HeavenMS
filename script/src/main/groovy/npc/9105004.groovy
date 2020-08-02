package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9105004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[][][] prizeTree = [[[2000002, 1002850], [20, 1]], [[2000006, 1012011], [20, 1]]]

   int state
   boolean gift
   int pqType

   EventManager em

   def start() {
      pqType = ((cm.getMapId() / 10) % 10) + 1
      state = (cm.getMapId() % 10 > 0) ? 1 : 0
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (state > 0) {
            insidePqAction(mode, type, selection)
         } else {
            recruitPqAction(mode, type, selection)
         }
      }
   }

   def recruitPqAction(Byte mode, Byte type, Integer selection) {
      if (status == 0) {
         em = cm.getEventManager("HolidayPQ_" + pqType)
         if (em == null) {
            cm.sendOk(I18nMessage.from("9105004_HOLIDAY_PQ_ENCOUNTERED_ERROR").with(pqType))

            cm.dispose()
         } else if (cm.isUsingOldPqNpcStyle()) {
            action((byte) 1, (byte) 0, 0)
            return
         }

         cm.sendSimple(I18nMessage.from("9105004_PARTY_QUEST_INFO").with(em.getProperty("party"), cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable"))

      } else if (status == 1) {
         if (selection == 0) {
            if (cm.getParty().isEmpty()) {
               cm.sendOk(I18nMessage.from("9105004_MUST_BE_IN_PARTY"))

               cm.dispose()
            } else if (!cm.isLeader()) {
               cm.sendOk(I18nMessage.from("9105004_PARTY_LEADER_MUST_START"))

               cm.dispose()
            } else {
               MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
               if (eli.size() > 0) {
                  if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), pqType)) {
                     cm.sendOk(I18nMessage.from("9105004_ANOTHER_PARTY"))

                  }
               } else {
                  cm.sendOk(I18nMessage.from("9105004_PARTY_REQUIREMENTS"))

               }

               cm.dispose()
            }
         } else if (selection == 1) {
            boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
            cm.sendOk(I18nMessage.from("9105004_PARTY_SEARCH_STATUS").with((psState ? "enabled" : "disabled")))

            cm.dispose()
         } else {
            cm.sendOk(I18nMessage.from("9105004_PARTY_QUEST_INFO_2"))

            cm.dispose()
         }
      }
   }

   def insidePqAction(Byte mode, Byte type, Integer selection) {
      EventInstanceManager eim = cm.getEventInstance()
      int difficulty = eim.getIntProperty("level")
      int stg = eim.getIntProperty("statusStg1")

      MapleMap map = eim.getInstanceMap(889100001 + 10 * (difficulty - 1))

      if (status == 0) {
         if (stg == -1) {
            cm.sendNext(I18nMessage.from("9105004_FINALLY_HERE"))

         } else if (stg == 0) {
            if (cm.getMap().getMonsterById(9400321 + 5 * difficulty) == null) {
               cm.sendNext(I18nMessage.from("9105004_DEFEAT_SCROOGE"))

               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("9105004_JUST_AS_I_EXPECTED"))

            }
         } else {
            if (!eim.isEventCleared()) {
               cm.sendNext(I18nMessage.from("9105004_DEFEAT_SCROOGE_2"))

               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("9105004_WOW"))

            }
         }
      } else if (status == 1) {
         if (stg == -1) {
            if (!cm.isEventLeader()) {
               cm.sendOk(I18nMessage.from("9105004_PARTY_LEADER_MUST_SPEAK"))

               cm.dispose()
               return
            }

            MapleLifeFactory.getMonster(9400317 + (5 * difficulty)).ifPresent({ snowman ->
               map.allowSummonState(true)
               map.spawnMonsterOnGroundBelow(snowman, new Point(-180, 15))
               eim.setIntProperty("snowmanLevel", 1)
               MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SNOWMAN_PROTECT"))
               eim.setIntProperty("statusStg1", 0)
            })

            cm.dispose()
         } else if (stg == 0) {
            if (!cm.isEventLeader()) {
               cm.sendOk(I18nMessage.from("9105004_PARTY_LEADER_MUST_SPEAK"))

               cm.dispose()
               return
            }

            MapleLifeFactory.getMonster(9400318 + difficulty).ifPresent({ boss ->
               MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.PINK_TEXT, I18nMessage.from("SCROOGE_SUMMONED"))
               eim.getEm().getIv().invokeFunction("snowmanHeal", eim)
               map.spawnMonsterOnGroundBelow(boss, new Point(-180, 15))
               eim.setProperty("spawnedBoss", "true")
               eim.setIntProperty("statusStg1", 1)
            })

            cm.dispose()
         } else {
            gift = cm.haveItem(4032092, 1)
            if (gift) {
               String optStr = generateSelectionMenu(generatePrizeString())
               cm.sendSimple("Oh, you brought a #b#t4032092##k with you? That's nice, hold on a bit... Here's your Maplemas gift. Please select the one you'd like to receive:\r\n\r\n" + optStr)
            } else if (eim.gridCheck(cm.getPlayer()) == -1) {
               cm.sendNext(I18nMessage.from("9105004_MAPLEMAS_GIFT"))

            } else {
               cm.sendOk(I18nMessage.from("9105004_HAPPY"))

               cm.dispose()
            }
         }

      } else if (status == 2) {
         if (gift) {
            int[][] selItems = prizeTree[selection]
            if (cm.canHoldAll(selItems[0], selItems[1])) {
               cm.gainItem(4032092, (short) -1)
               cm.gainItem(selItems[0][0], (short) selItems[1][0])

               if (selection == 1) {
                  int rnd = (Math.random() * 9) | 0
                  cm.gainItem(selItems[0][1] + rnd, (short) selItems[1][1])
               } else {
                  cm.gainItem(selItems[0][1], (short) selItems[1][1])
               }
            } else {
               cm.sendOk(I18nMessage.from("9105004_MAKE_EQUIP_AND_USE_ROOM"))

            }
         } else {
            if (eim.giveEventReward(cm.getPlayer(), difficulty)) {
               eim.gridInsert(cm.getPlayer(), 1)
            } else {
               cm.sendOk(I18nMessage.from("9105004_MAKE_EQUIP_USE_AND_ETC_ROOM"))

            }
         }

         cm.dispose()
      }
   }

   def generatePrizeString() {
      String[] strTree = []

      for (int i = 0; i < prizeTree.length; i++) {
         int[] items = prizeTree[i][0]
         int[] qtys = prizeTree[i][1]

         String strSel = ""
         for (int j = 0; j < items.length; j++) {
            strSel += ("#i" + items[j] + "# #t" + items[j] + "#" + (qtys[j] > 1 ? (" : " + qtys[j]) : ""))
         }

         strTree << strSel
      }

      return strTree
   }

   static def generateSelectionMenu(String[] array) {
      String menu = ""
      for (int i = 0; i < array.length; i++) {
         menu += "#L" + i + "#" + array[i] + "#l\r\n"
      }
      return menu
   }
}

NPC9105004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9105004(cm: cm))
   }
   return (NPC9105004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }