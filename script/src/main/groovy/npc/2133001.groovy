package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2133001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int mapId

   def start() {
      mapId = cm.getPlayer().getMapId()

      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            String ellinStr = ellinMapMessage(mapId)

            if (mapId == 930000000) {
               cm.sendNext(ellinStr)
            } else if (mapId == 930000300) {
               EventInstanceManager eim = cm.getEventInstance()

               if (eim.getIntProperty("statusStg4") == 0) {
                  eim.showClearEffect(cm.getMap().getId())
                  eim.setIntProperty("statusStg4", 1)
               }

               cm.sendNext(ellinStr)
            } else if (mapId == 930000400) {
               if (cm.haveItem(4001169, 20)) {
                  if (cm.isEventLeader()) {
                     cm.sendNext(I18nMessage.from("2133001_SHALL_WE_PROCEED"))
                  } else {
                     cm.sendOk(I18nMessage.from("2133001_LET_THE_LEADER_HAND_ME"))
                     cm.dispose()
                  }
               } else {
                  if (cm.getEventInstance().gridCheck(cm.getPlayer()) != 1) {
                     cm.sendNext(ellinStr)

                     cm.getEventInstance().gridInsert(cm.getPlayer(), 1)
                     status = -1
                  } else {
                     int mobs = cm.getMap().countMonsters()

                     if (mobs > 0) {
                        if (!cm.haveItem(2270004)) {
                           if (cm.canHold(2270004, 10)) {
                              cm.gainItem(2270004, (short) 10)
                              cm.sendOk(I18nMessage.from("2133001_USE_THE_ITEM_TO_CAPTURE"))
                              cm.dispose()
                           } else {
                              cm.sendOk(I18nMessage.from("2133001_MAKE_USE_SPACE"))
                              cm.dispose()
                           }
                        } else {
                           cm.sendYesNo(ellinStr + "\r\n\r\nIt may be you are #rwilling to quit#k? Please double-think it, maybe your partners are still trying this instance.")
                        }
                     } else {
                        cm.sendYesNo(I18nMessage.from("2133001_PARTY_MEMBER_OPTIONS"))
                     }
                  }
               }
            } else {
               cm.sendYesNo(ellinStr + "\r\n\r\nIt may be you are #rwilling to quit#k? Please double-think it, maybe your partners are still trying this instance.")
            }
         } else if (status == 1) {
            if (!(mapId == 930000000)) {
               if (mapId == 930000300) {
                  cm.getEventInstance().warpEventTeam(930000400)
               } else if (mapId == 930000400) {
                  if (cm.haveItem(4001169, 20) && cm.isEventLeader()) {
                     cm.gainItem(4001169, (short) -20)
                     cm.getEventInstance().warpEventTeam(930000500)
                  } else {
                     cm.warp(930000800, 0)
                  }
               } else {
                  cm.warp(930000800, 0)
               }
            }

            cm.dispose()
         }
      }
   }

   static def ellinMapMessage(mapId) {
      switch (mapId) {
         case 930000000:
            return "Welcome to the Forest of Poison Haze. Proceed by entering the portal."

         case 930000100:
            return "The #b#o9300172##k have taken the area. We have to eliminate all these contaminated monsters to proceed further."

         case 930000200:
            return "A great spine has blocked the way ahead. To remove this barrier we must retrieve the poison the #b#o9300173##k carries to deter the overgrown spine. However, the poison in natural state can't be handled, as it is way too concentrated. Use the #bfountain#k over there to dilute it."

         case 930000300:
            return "Oh great, you have reached me. We can now proceed further inside the forest."

         case 930000400:
            return "The #b#o9300175##k took over this area. However they are not ordinary monsters, then regrow pretty fast, #rnormal weapon and magic does no harm to it#k at all. We have to purify all these contaminated monsters, using #b#t2270004##k! Let your group leader get me 20 Monster Marbles from them."

         case 930000600:
            return "The root of all problems of the forest! Place the obtained Magic Stone on the Altar and prepare yourselves!"

         case 930000700:
            return "This is it, you guys did it! Thank you so much for purifying the forest!!"

      }
   }
}

NPC2133001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2133001(cm: cm))
   }
   return (NPC2133001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }