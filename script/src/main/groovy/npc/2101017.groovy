package npc

import constants.game.GameConstants
import scripting.npc.NPCConversationManager
import server.expeditions.MapleExpedition
import server.expeditions.MapleExpeditionType
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101017 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   MapleExpeditionType expeditionType
   MapleExpedition expedition
   List<Map.Entry<Integer, String>> expeditionMembers

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {

      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }

         if (cm.getPlayer().getMapId() == 980010100 || cm.getPlayer().getMapId() == 980010200 || cm.getPlayer().getMapId() == 980010300) {
            if (cm.getPlayer().getMapId() == 980010100) {
               expeditionType = MapleExpeditionType.ARIANT
               expedition = cm.getExpedition(expeditionType)

            } else if (cm.getPlayer().getMapId() == 980010200) {
               expeditionType = MapleExpeditionType.ARIANT1
               expedition = cm.getExpedition(expeditionType)
            } else {
               expeditionType = MapleExpeditionType.ARIANT2
               expedition = cm.getExpedition(expeditionType)
            }

            if (expedition == null) {
               cm.dispose()
               return
            }

            expeditionMembers = expedition.getMemberList()
            if (status == 0) {
               if (cm.isLeaderExpedition(expeditionType)) {
                  cm.sendSimple("What would you like to do? #b\r\n#L1#View current members#l\r\n#L2#Ban members#l\r\n#L3#Start the battle#l\r\n#L4#Leave the arena#l")
                  status = 1
               } else {
                  String toSend = "Current members inside this arena:\r\n#b"
                  toSend += cm.getExpeditionMemberNames(expeditionType)
                  cm.sendOk(toSend)
                  cm.dispose()
               }
            } else if (status == 1) {
               if (selection == 1) {
                  String toSend = "Current members inside this arena:\r\n#b"
                  toSend += cm.getExpeditionMemberNames(expeditionType)
                  cm.sendOk(toSend)
                  cm.dispose()
               } else if (selection == 2) {
                  int size = expeditionMembers.size()
                  if (size == 1) {
                     cm.sendOk("You are the only member of the expedition.")
                     cm.dispose()
                     return
                  }
                  String text = "The following members make up your expedition (Click on them to expel them):\r\n"
                  text += "\r\n\t\t1." + expedition.getLeader().getName()
                  for (int i = 1; i < size; i++) {
                     text += "\r\n#b#L" + (i + 1) + "#" + (i + 1) + ". " + expeditionMembers.get(i).getValue() + "#l\n"
                  }
                  cm.sendSimple(text)
                  status = 6
               } else if (selection == 3) {
                  if (expedition.getMembers().size() < 1) {
                     cm.sendOk("Need one more players to start the battle.")
                     cm.dispose()
                  } else {
                     if (cm.getParty() != null) {
                        cm.sendOk("You cannot enter the battle as a party group.")
                        cm.dispose()
                        return
                     }

                     String errorMsg = cm.startAriantBattle(expeditionType, cm.getPlayer().getMapId())
                     if (errorMsg != "") {
                        cm.sendOk(errorMsg)
                     }

                     cm.dispose()
                  }
               } else if (selection == 4) {
                  MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ARENA_LEADER_LEFT"))
                  expedition.warpExpeditionTeam(980010000)
                  cm.endExpedition(expedition)
                  cm.dispose()
               }
            } else if (status == 6) {
               if (selection > 0) {
                  Map.Entry<Integer, String> banned = expeditionMembers.get(selection - 1)
                  expedition.ban(banned)
                  cm.sendOk("You have banned " + banned.getValue() + " from the expedition.")
                  cm.dispose()
               } else {
                  cm.sendSimple(list)
                  status = 2
               }
            }
         } else if (GameConstants.isAriantColiseumArena(cm.getPlayer().getMapId())) {
            if (cm.getPlayer().getMapId() == 980010101) {
               expeditionType = MapleExpeditionType.ARIANT
               expedition = cm.getExpedition(expeditionType)
            } else if (cm.getPlayer().getMapId() == 980010201) {
               expeditionType = MapleExpeditionType.ARIANT1
               expedition = cm.getExpedition(expeditionType)
            } else {
               expeditionType = MapleExpeditionType.ARIANT2
               expedition = cm.getExpedition(expeditionType)
            }
            if (status == 0) {
               String gotTheBombs = expedition.getProperty("gotBomb" + cm.getChar().getId())
               if (gotTheBombs != null) {
                  cm.sendOk("I already gave you the bomb, please kill the #bScorpio#k now!")
                  cm.dispose()
               } else if (cm.canHoldAll([2270002, 2100067], [50, 5])) {
                  cm.sendOk("I have given you (5) #b#eBombs#k#n and (50) #b#eElement Rock#k#n.\r\nUse the Elementary Rocks to capture the scorpions for #r#eSpirit Jewels#k#n!")
                  expedition.setProperty("gotBomb" + cm.getChar().getId(), "1")
                  cm.gainItem(2270002, (short) 50)
                  cm.gainItem(2100067, (short) 5)
                  cm.dispose()
               } else {
                  cm.sendOk("It seems that your inventory is full.")
                  cm.dispose()
               }
            }
         } else {
            cm.sendOk("Hi there, have you heard of the Ariant Coliseum Battle Arena, it's a competitive event available to players between level 20 to 30!")
            cm.dispose()
         }
      }
   }
}

NPC2101017 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101017(cm: cm))
   }
   return (NPC2101017) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }