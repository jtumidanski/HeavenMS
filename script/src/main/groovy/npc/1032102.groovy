package npc

import client.inventory.manipulator.MapleInventoryManipulator
import constants.MapleInventoryType
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032102 {
    NPCConversationManager cm
    int status = -1
    int sel = -1

    def start() {
        status = -1
        action((byte) 1, (byte) 0, 0)
    }

    def action(Byte mode, Byte type, Integer selection) {
        if (mode == -1) {
            cm.dispose()
        } else {
            if (mode == 0 && type > 0) {
                cm.sendOk(I18nMessage.from("1032102_SEE_YOU_NEXT_TIME"))
                cm.dispose()
                return
            }
            if (mode == 1) {
                status++
            } else {
                status--
            }

            if (status == 0) {
                cm.sendYesNo(I18nMessage.from("1032102_HELLO"))
            } else if (status == 1) {
                if (cm.haveItem(5000028, 1)) {
                    cm.gainItem(5000028, (short) -1)
                    cm.gainItem(5000029, (short) 1)
                    cm.sendOk(I18nMessage.from("1032102_IT_HAS_HATCHED"))
                    cm.dispose()
                } else if (cm.getPlayer().getPet(0) == null) {
                    cm.sendOk(I18nMessage.from("1032102_PET_EQUIPPED_ON_SLOT_1"))
                    cm.dispose()
                } else if (cm.getPlayer().getPet(0).id() < 5000029 || cm.getPlayer().getPet(0).id() > 5000033 || !cm.haveItem(5380000, 1)) {
                    cm.sendOk(I18nMessage.from("1032102_DO_NOT_MEET_THE_REQUIREMENTS"))
                    cm.dispose()
                } else if (cm.getPlayer().getPet(0).level() < 15) {
                    cm.sendOk(I18nMessage.from("1032102_PET_LEVEL_15_OR_ABOVE"))
                    cm.dispose()
                } else if (cm.haveItem(5000029, 2) || cm.haveItem(5000030, 2) || cm.haveItem(5000031, 2) || cm.haveItem(5000032, 2) || cm.haveItem(5000033, 2)) {
                    cm.sendSimple(I18nMessage.from("1032102_REMOVE_ONE"))
                } else {
                    int i

                    for (i = 0; i < 3; i++) {
                        if (cm.getPlayer().getPet(i) != null && cm.getPlayer().getPet(i).id() == 5000029) {
                            break
                        }
                    }
                    if (i == 3) {
                        cm.sendOk(I18nMessage.from("1032102_PET_DRAGON_NOT_READY_OR_MISSING_ITEM"))
                        cm.dispose()
                        return
                    }

                    int id = cm.getPlayer().getPet(i).id()
                    //var name = cm.getPlayer().getPet(i).getName();
                    //var level = cm.getPlayer().getPet(i).getLevel();
                    //var closeness = cm.getPlayer().getPet(i).getCloseness();
                    //var fullness = cm.getPlayer().getPet(i).getFullness();
                    //MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    if (id < 5000029 || id > 5000033) {
                        cm.sendOk(I18nMessage.from("1032102_SOMETHING_WRONG"))
                        cm.dispose()
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
                        cm.sendOk(I18nMessage.from("1032102_SOMETHING_WRONG"))
                        cm.dispose()
                    }

                    /*if (name.equals(MapleItemInformationProvider.getInstance().getName(id))) {
     name = MapleItemInformationProvider.getInstance().getName(after);
     }*/

                    cm.gainItem(5380000, (short) -1)
                    cm.evolvePet((byte) i, after)

                    cm.sendOk(I18nMessage.from("1032102_DRAGON_HAS_EVOLVED").with(id, id, after, after))
                    cm.dispose()
                }
            } else if (status == 2) {
                if (selection == 0) {
                    MapleInventoryManipulator.removeFromSlot(cm.getClient(), MapleInventoryType.CASH, (short) 1, (short) 1, true)
                    cm.sendOk(I18nMessage.from("1032102_FIRSTCASH_SLOT_REMOVED"))
                } else if (selection == 1) {
                    if (cm.haveItem(5000029, 2)) {
                        cm.gainItem(5000029, (short) -1)
                    } else if (cm.haveItem(5000030, 2)) {
                        cm.gainItem(5000030, (short) -1)
                    } else if (cm.haveItem(5000031, 2)) {
                        cm.gainItem(5000031, (short) -1)
                    } else if (cm.haveItem(5000032, 2)) {
                        cm.gainItem(5000032, (short) -1)
                    } else if (cm.haveItem(5000033, 2)) {
                        cm.gainItem(5000033, (short) -1)
                    }
                    cm.sendOk(I18nMessage.from("1032102_DRAGON_REMOVED"))
                } else if (selection == 2) {
                    cm.sendOk(I18nMessage.from("1032102_COME_BACK_NEXT_TIME"))
                }
                cm.dispose()
            }
        }
    }
}

NPC1032102 getNPC() {
    if (!getBinding().hasVariable("npc")) {
        NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
        getBinding().setVariable("npc", new NPC1032102(cm: cm))
    }
    return (NPC1032102) getBinding().getVariable("npc")
}

def start() {
    getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }