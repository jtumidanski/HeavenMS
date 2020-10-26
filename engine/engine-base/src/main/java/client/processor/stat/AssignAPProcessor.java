package client.processor.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.SkillFactory;
import client.autoban.AutoBanFactory;
import client.inventory.Equip;
import client.inventory.Item;
import config.YamlConfig;
import constants.MapleInventoryType;
import constants.MapleJob;
import constants.skills.BlazeWizard;
import constants.skills.Brawler;
import constants.skills.DawnWarrior;
import constants.skills.Magician;
import constants.skills.ThunderBreaker;
import constants.skills.Warrior;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.stat.EnableActions;
import tools.packet.stat.UpdatePlayerStats;

public class AssignAPProcessor {

   public static void APAutoAssignAction(MapleClient c, byte job, int[] types, int[] gains) {
      MapleCharacter chr = c.getPlayer();
      if (chr.getRemainingAp() < 1) {
         return;
      }

      Collection<Item> equippedC = chr.getInventory(MapleInventoryType.EQUIPPED).list();

      c.lockClient();
      try {
         int[] statGain = new int[4];
         int[] statUpdate = new int[4];
         statGain[0] = 0;
         statGain[1] = 0;
         statGain[2] = 0;
         statGain[3] = 0;

         int remainingAp = chr.getRemainingAp();

         if (YamlConfig.config.server.USE_SERVER_AUTOASSIGNER) {
            // This method excels for assigning APs in such a way to cover all equipments AP requirements.
            int str = 0, dex = 0, luk = 0, int_ = 0;
            List<Short> eqpStrList = new ArrayList<>();
            List<Short> eqpDexList = new ArrayList<>();
            List<Short> eqpLukList = new ArrayList<>();

            Equip nEquip;

            for (Item item : equippedC) {   //selecting the biggest AP value of each stat from each equipped item.
               nEquip = (Equip) item;
               if (nEquip.str() > 0) {
                  eqpStrList.add((short) nEquip.str());
               }
               str += nEquip.str();

               if (nEquip.dex() > 0) {
                  eqpDexList.add((short) nEquip.dex());
               }
               dex += nEquip.dex();

               if (nEquip.luk() > 0) {
                  eqpLukList.add((short) nEquip.luk());
               }
               luk += nEquip.luk();

               //if(nEquip.getInt() > 0) eqpIntList.add(nEquip.getInt()); //not needed...
               int_ += nEquip.intelligence();
            }

            statUpdate[0] = chr.getStr();
            statUpdate[1] = chr.getDex();
            statUpdate[2] = chr.getLuk();
            statUpdate[3] = chr.getInt();

            eqpStrList.sort(Collections.reverseOrder());
            eqpDexList.sort(Collections.reverseOrder());
            eqpLukList.sort(Collections.reverseOrder());

            //Auto assigner looks up the 1st/2nd placed equips for their stats to calculate the optimal upgrade.
            int eqpStr = getNthHighestStat(eqpStrList, (short) 0) + getNthHighestStat(eqpStrList, (short) 1);
            int eqpDex = getNthHighestStat(eqpDexList, (short) 0) + getNthHighestStat(eqpDexList, (short) 1);
            int eqpLuk = getNthHighestStat(eqpLukList, (short) 0) + getNthHighestStat(eqpLukList, (short) 1);

            //c.getPlayer().message("----------------------------------------");
            //c.getPlayer().message("SDL: s" + eqpStr + " d" + eqpDex + " l" + eqpLuk + " BASE STATS --> STR: " + chr.getStr() + " DEX: " + chr.getDex() + " INT: " + chr.getInt() + " LUK: " + chr.getLuk());
            //c.getPlayer().message("SUM EQUIP STATS -> STR: " + str + " DEX: " + dex + " LUK: " + luk + " INT: " + int_);

            MapleJob stance = c.getPlayer().getJobStyle(job);
            int prStat, scStat, trStat = 0, temp, tempAp = remainingAp, CAP;
            if (tempAp < 1) {
               return;
            }

            MapleStat primary, secondary, tertiary = MapleStat.LUK;
            switch (stance) {
               case MAGICIAN:
                  CAP = 165;
                  scStat = (chr.getLevel() + 3) - (chr.getLuk() + luk - eqpLuk);
                  if (scStat < 0) {
                     scStat = 0;
                  }
                  scStat = Math.min(scStat, tempAp);

                  if (tempAp > scStat) {
                     tempAp -= scStat;
                  } else {
                     tempAp = 0;
                  }

                  prStat = tempAp;
                  int_ = prStat;
                  luk = scStat;
                  str = 0;
                  dex = 0;

                  if (YamlConfig.config.server.USE_AUTOASSIGN_SECONDARY_CAP && luk + chr.getLuk() > CAP) {
                     temp = luk + chr.getLuk() - CAP;
                     scStat -= temp;
                     prStat += temp;
                  }

                  primary = MapleStat.INT;
                  secondary = MapleStat.LUK;
                  tertiary = MapleStat.DEX;

                  break;

               case BOWMAN:
                  CAP = 125;
                  scStat = (chr.getLevel() + 5) - (chr.getStr() + str - eqpStr);
                  if (scStat < 0) {
                     scStat = 0;
                  }
                  scStat = Math.min(scStat, tempAp);

                  if (tempAp > scStat) {
                     tempAp -= scStat;
                  } else {
                     tempAp = 0;
                  }

                  prStat = tempAp;
                  dex = prStat;
                  str = scStat;
                  int_ = 0;
                  luk = 0;

                  if (YamlConfig.config.server.USE_AUTOASSIGN_SECONDARY_CAP && str + chr.getStr() > CAP) {
                     temp = str + chr.getStr() - CAP;
                     scStat -= temp;
                     prStat += temp;
                  }

                  primary = MapleStat.DEX;
                  secondary = MapleStat.STR;

                  break;

               case GUNSLINGER:
               case CROSSBOWMAN:
                  CAP = 120;
                  scStat = chr.getLevel() - (chr.getStr() + str - eqpStr);
                  if (scStat < 0) {
                     scStat = 0;
                  }
                  scStat = Math.min(scStat, tempAp);

                  if (tempAp > scStat) {
                     tempAp -= scStat;
                  } else {
                     tempAp = 0;
                  }

                  prStat = tempAp;
                  dex = prStat;
                  str = scStat;
                  int_ = 0;
                  luk = 0;

                  if (YamlConfig.config.server.USE_AUTOASSIGN_SECONDARY_CAP && str + chr.getStr() > CAP) {
                     temp = str + chr.getStr() - CAP;
                     scStat -= temp;
                     prStat += temp;
                  }

                  primary = MapleStat.DEX;
                  secondary = MapleStat.STR;

                  break;

               case THIEF:
                  CAP = 160;

                  scStat = 0;
                  if (chr.getDex() < 80) {
                     scStat = (2 * chr.getLevel()) - (chr.getDex() + dex - eqpDex);
                     if (scStat < 0) {
                        scStat = 0;
                     }

                     scStat = Math.min(80 - chr.getDex(), scStat);
                     scStat = Math.min(tempAp, scStat);
                     tempAp -= scStat;
                  }

                  temp = (chr.getLevel() + 40) - Math.max(80, scStat + chr.getDex() + dex - eqpDex);
                  if (temp < 0) {
                     temp = 0;
                  }
                  temp = Math.min(tempAp, temp);
                  scStat += temp;
                  tempAp -= temp;

                  // thieves will upgrade STR as well only if a level-based threshold is reached.
                  if (chr.getStr() >= Math.max(13, (int) (0.4 * chr.getLevel()))) {
                     if (chr.getStr() < 50) {
                        trStat = (chr.getLevel() - 10) - (chr.getStr() + str - eqpStr);
                        if (trStat < 0) {
                           trStat = 0;
                        }

                        trStat = Math.min(50 - chr.getStr(), trStat);
                        trStat = Math.min(tempAp, trStat);
                        tempAp -= trStat;
                     }

                     temp = (20 + (chr.getLevel() / 2)) - Math.max(50, trStat + chr.getStr() + str - eqpStr);
                     if (temp < 0) {
                        temp = 0;
                     }
                     temp = Math.min(tempAp, temp);
                     trStat += temp;
                     tempAp -= temp;
                  }

                  prStat = tempAp;
                  luk = prStat;
                  dex = scStat;
                  str = trStat;
                  int_ = 0;

                  if (YamlConfig.config.server.USE_AUTOASSIGN_SECONDARY_CAP && dex + chr.getDex() > CAP) {
                     temp = dex + chr.getDex() - CAP;
                     scStat -= temp;
                     prStat += temp;
                  }
                  if (YamlConfig.config.server.USE_AUTOASSIGN_SECONDARY_CAP && str + chr.getStr() > CAP) {
                     temp = str + chr.getStr() - CAP;
                     trStat -= temp;
                     prStat += temp;
                  }

                  primary = MapleStat.LUK;
                  secondary = MapleStat.DEX;
                  tertiary = MapleStat.STR;

                  break;

               case BRAWLER:
               default:    //warrior, beginner, ...
                  CAP = 300;

                  boolean highDex = false;
                  if (chr.getLevel() < 40) {
                     if (chr.getDex() >= (2 * chr.getLevel()) + 2) {
                        highDex = true;
                     }
                  } else {
                     if (chr.getDex() >= chr.getLevel() + 42) {
                        highDex = true;
                     }
                  }

                  // other classes will start favoring more DEX only if a level-based threshold is reached.
                  scStat = 0;
                  if (!highDex) {
                     if (chr.getDex() < 80) {
                        scStat = (2 * chr.getLevel()) - (chr.getDex() + dex - eqpDex);
                        if (scStat < 0) {
                           scStat = 0;
                        }

                        scStat = Math.min(80 - chr.getDex(), scStat);
                        scStat = Math.min(tempAp, scStat);
                        tempAp -= scStat;
                     }

                     temp = (chr.getLevel() + 40) - Math.max(80, scStat + chr.getDex() + dex - eqpDex);
                  } else {
                     if (chr.getDex() < 96) {
                        scStat = (int) (2.4 * chr.getLevel()) - (chr.getDex() + dex - eqpDex);
                        if (scStat < 0) {
                           scStat = 0;
                        }

                        scStat = Math.min(96 - chr.getDex(), scStat);
                        scStat = Math.min(tempAp, scStat);
                        tempAp -= scStat;
                     }

                     temp = 96 + (int) (1.2 * (chr.getLevel() - 40)) - Math.max(96, scStat + chr.getDex() + dex - eqpDex);
                  }
                  if (temp < 0) {
                     temp = 0;
                  }
                  temp = Math.min(tempAp, temp);
                  scStat += temp;
                  tempAp -= temp;

                  prStat = tempAp;
                  str = prStat;
                  dex = scStat;
                  int_ = 0;
                  luk = 0;

                  if (YamlConfig.config.server.USE_AUTOASSIGN_SECONDARY_CAP && dex + chr.getDex() > CAP) {
                     temp = dex + chr.getDex() - CAP;
                     scStat -= temp;
                     prStat += temp;
                  }

                  primary = MapleStat.STR;
                  secondary = MapleStat.DEX;
            }

            //-------------------------------------------------------------------------------------

            int extras = 0;

            extras = gainStatByType(primary, statGain, prStat + extras, statUpdate);
            extras = gainStatByType(secondary, statGain, scStat + extras, statUpdate);
            extras = gainStatByType(tertiary, statGain, trStat + extras, statUpdate);

            if (extras > 0) {    //redistribute surplus in priority order
               extras = gainStatByType(primary, statGain, extras, statUpdate);
               extras = gainStatByType(secondary, statGain, extras, statUpdate);
               extras = gainStatByType(tertiary, statGain, extras, statUpdate);
               gainStatByType(getQuaternaryStat(stance), statGain, extras, statUpdate);
            }

            chr.assignStrDexIntLuk(statGain[0], statGain[1], statGain[3], statGain[2]);
            PacketCreator.announce(c, new EnableActions());

            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("ASSIGN_AP_SUGGESTION").with(statGain[0], statGain[1], statGain[3], statGain[2]));
         } else {
            if (types.length != 2 || gains.length != 2) {
               AutoBanFactory.PACKET_EDIT.alert(chr, "Didn't send full packet for Auto Assign.");
               c.disconnect(true, false);
               return;
            }

            for (int i = 0; i < 2; i++) {
               int type = types[i];
               int tempVal = gains[i];
               if (tempVal < 0 || tempVal > remainingAp) {
                  return;
               }

               gainStatByType(MapleStat.getBy5ByteEncoding(type), statGain, tempVal, statUpdate);
            }

            chr.assignStrDexIntLuk(statGain[0], statGain[1], statGain[3], statGain[2]);
            PacketCreator.announce(c, new EnableActions());
         }
      } finally {
         c.unlockClient();
      }
   }

   private static int getNthHighestStat(List<Short> statList, short rank) {    // ranks from 0
      return (statList.size() <= rank ? 0 : statList.get(rank));
   }

   private static int gainStatByType(MapleStat type, int[] statGain, int gain, int[] statUpdate) {
      if (gain <= 0) {
         return 0;
      }

      int newVal = 0;
      if (type.equals(MapleStat.STR)) {
         newVal = statUpdate[0] + gain;
         if (newVal > YamlConfig.config.server.MAX_AP) {
            statGain[0] += (gain - (newVal - YamlConfig.config.server.MAX_AP));
            statUpdate[0] = YamlConfig.config.server.MAX_AP;
         } else {
            statGain[0] += gain;
            statUpdate[0] = newVal;
         }
      } else if (type.equals(MapleStat.INT)) {
         newVal = statUpdate[3] + gain;
         if (newVal > YamlConfig.config.server.MAX_AP) {
            statGain[3] += (gain - (newVal - YamlConfig.config.server.MAX_AP));
            statUpdate[3] = YamlConfig.config.server.MAX_AP;
         } else {
            statGain[3] += gain;
            statUpdate[3] = newVal;
         }
      } else if (type.equals(MapleStat.LUK)) {
         newVal = statUpdate[2] + gain;
         if (newVal > YamlConfig.config.server.MAX_AP) {
            statGain[2] += (gain - (newVal - YamlConfig.config.server.MAX_AP));
            statUpdate[2] = YamlConfig.config.server.MAX_AP;
         } else {
            statGain[2] += gain;
            statUpdate[2] = newVal;
         }
      } else if (type.equals(MapleStat.DEX)) {
         newVal = statUpdate[1] + gain;
         if (newVal > YamlConfig.config.server.MAX_AP) {
            statGain[1] += (gain - (newVal - YamlConfig.config.server.MAX_AP));
            statUpdate[1] = YamlConfig.config.server.MAX_AP;
         } else {
            statGain[1] += gain;
            statUpdate[1] = newVal;
         }
      }

      if (newVal > YamlConfig.config.server.MAX_AP) {
         return newVal - YamlConfig.config.server.MAX_AP;
      }
      return 0;
   }

   private static MapleStat getQuaternaryStat(MapleJob stance) {
      if (stance != MapleJob.MAGICIAN) {
         return MapleStat.INT;
      }
      return MapleStat.STR;
   }

   public static boolean APResetAction(MapleClient c, int APFrom, int APTo) {
      c.lockClient();
      try {
         MapleCharacter player = c.getPlayer();

         switch (APFrom) {
            case 64: // str
               if (player.getStr() < 5) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MISSING_MINIMUM").with("STR"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               if (!player.assignStr(-1)) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_RESET_ERROR"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               break;
            case 128: // dex
               if (player.getDex() < 5) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MISSING_MINIMUM").with("DEX"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               if (!player.assignDex(-1)) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_RESET_ERROR"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               break;
            case 256: // int
               if (player.getInt() < 5) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MISSING_MINIMUM").with("INT"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               if (!player.assignInt(-1)) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_RESET_ERROR"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               break;
            case 512: // luk
               if (player.getLuk() < 5) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MISSING_MINIMUM").with("LUK"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               if (!player.assignLuk(-1)) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_RESET_ERROR"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
               break;
            case 2048: // HP
               if (YamlConfig.config.server.USE_ENFORCE_HPMP_SWAP) {
                  if (APTo != 8192) {
                     MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_HP_MP_SWAP_ENFORCEMENT"));
                     PacketCreator.announce(c, new EnableActions());
                     return false;
                  }
               }

               if (player.getHpMpApUsed() < 1) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_NOT_ENOUGH_HP_MP"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }

               int hp = player.getMaxHp();
               int level_ = player.getLevel();
               if (hp < level_ * 14 + 148) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MINIMUM_HP_POOL"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }

               int curHp = player.getHp();
               int hpLoss = -takeHp(player.getJob());
               player.assignHP(hpLoss, -1);
               if (!YamlConfig.config.server.USE_FIXED_RATIO_HPMP_UPDATE) {
                  player.updateHp(Math.max(1, curHp + hpLoss));
               }

               break;
            case 8192: // MP
               if (YamlConfig.config.server.USE_ENFORCE_HPMP_SWAP) {
                  if (APTo != 2048) {
                     MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MP_HP_SWAP_ENFORCEMENT"));
                     PacketCreator.announce(c, new EnableActions());
                     return false;
                  }
               }

               if (player.getHpMpApUsed() < 1) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_NOT_ENOUGH_HP_MP"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }

               int mp = player.getMaxMp();
               int level = player.getLevel();
               MapleJob job = player.getJob();

               boolean canWash = true;
               if (job.isA(MapleJob.SPEARMAN) && mp < 4 * level + 156) {
                  canWash = false;
               } else if ((job.isA(MapleJob.FIGHTER) || job.isA(MapleJob.ARAN1)) && mp < 4 * level + 56) {
                  canWash = false;
               } else if (job.isA(MapleJob.THIEF) && job.getId() % 100 > 0 && mp < level * 14 - 4) {
                  canWash = false;
               } else if (mp < level * 14 + 148) {
                  canWash = false;
               }

               if (!canWash) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_MINIMUM_MP_POOL"));
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }

               int curMp = player.getMp();
               int mpLoss = -takeMp(job);
               player.assignMP(mpLoss, -1);
               if (!YamlConfig.config.server.USE_FIXED_RATIO_HPMP_UPDATE) {
                  player.updateMp(Math.max(0, curMp + mpLoss));
               }
               break;
            default:
               PacketCreator.announce(c, new UpdatePlayerStats(Collections.emptyList(), true, player));
               return false;
         }

         addStat(player, APTo, true);
         return true;
      } finally {
         c.unlockClient();
      }
   }

   public static void APAssignAction(MapleClient c, int num) {
      c.lockClient();
      try {
         addStat(c.getPlayer(), num, false);
      } finally {
         c.unlockClient();
      }
   }

   private static boolean addStat(MapleCharacter chr, int apTo, boolean usedAPReset) {
      switch (apTo) {
         case 64:
            if (!chr.assignStr(1)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_ASSIGN_ERROR"));
               PacketCreator.announce(chr, new EnableActions());
               return false;
            }
            break;
         case 128: // Dex
            if (!chr.assignDex(1)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_ASSIGN_ERROR"));
               PacketCreator.announce(chr, new EnableActions());
               return false;
            }
            break;
         case 256: // Int
            if (!chr.assignInt(1)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_ASSIGN_ERROR"));
               PacketCreator.announce(chr, new EnableActions());
               return false;
            }
            break;
         case 512: // Luk
            if (!chr.assignLuk(1)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_ASSIGN_ERROR"));
               PacketCreator.announce(chr, new EnableActions());
               return false;
            }
            break;
         case 2048:
            if (!chr.assignHP(calcHpChange(chr, usedAPReset), 1)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_ASSIGN_ERROR"));
               PacketCreator.announce(chr, new EnableActions());
               return false;
            }
            break;
         case 8192:
            if (!chr.assignMP(calcMpChange(chr, usedAPReset), 1)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ASSIGN_AP_ASSIGN_ERROR"));
               PacketCreator.announce(chr, new EnableActions());
               return false;
            }
            break;
         default:
            PacketCreator.announce(chr, new UpdatePlayerStats(Collections.emptyList(), true, chr));
            return false;
      }
      return true;
   }

   private static int calcHpChange(MapleCharacter player, boolean usedAPReset) {
      MapleJob job = player.getJob();
      int MaxHP = 0;

      if (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.DAWN_WARRIOR_1)) {
         if (!usedAPReset) {
            int skillId = job.isA(MapleJob.DAWN_WARRIOR_1) ? DawnWarrior.MAX_HP_INCREASE : Warrior.IMPROVED_MAX_HP;
            MaxHP += SkillFactory.applyIfHasSkill(player, skillId, (skill, skillLevel) -> skill.getEffect(skillLevel).getY(), 0);
         }

         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (usedAPReset) {
               MaxHP += 20;
            } else {
               MaxHP += Randomizer.rand(18, 22);
            }
         } else {
            MaxHP += 20;
         }
      } else if (job.isA(MapleJob.ARAN1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (usedAPReset) {
               MaxHP += 20;
            } else {
               MaxHP += Randomizer.rand(26, 30);
            }
         } else {
            MaxHP += 28;
         }
      } else if (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.BLAZE_WIZARD_1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (usedAPReset) {
               MaxHP += 6;
            } else {
               MaxHP += Randomizer.rand(5, 9);
            }
         } else {
            MaxHP += 6;
         }
      } else if (job.isA(MapleJob.THIEF) || job.isA(MapleJob.NIGHT_WALKER_1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (usedAPReset) {
               MaxHP += 16;
            } else {
               MaxHP += Randomizer.rand(14, 18);
            }
         } else {
            MaxHP += 16;
         }
      } else if (job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.WIND_ARCHER_1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (usedAPReset) {
               MaxHP += 16;
            } else {
               MaxHP += Randomizer.rand(14, 18);
            }
         } else {
            MaxHP += 16;
         }
      } else if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDER_BREAKER_1)) {
         if (!usedAPReset) {
            MaxHP += SkillFactory.applyIfHasSkill(player, job.isA(MapleJob.PIRATE) ? Brawler.IMPROVE_MAX_HP : ThunderBreaker.IMPROVE_MAX_HP, (skill, skillLevel) -> skill.getEffect(skillLevel).getY(), 0);
         }

         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (usedAPReset) {
               MaxHP += 18;
            } else {
               MaxHP += Randomizer.rand(16, 20);
            }
         } else {
            MaxHP += 18;
         }
      } else if (usedAPReset) {
         MaxHP += 8;
      } else {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            MaxHP += Randomizer.rand(8, 12);
         } else {
            MaxHP += 10;
         }
      }

      return MaxHP;
   }

   private static int calcMpChange(MapleCharacter player, boolean usedAPReset) {
      MapleJob job = player.getJob();
      int MaxMP = 0;

      if (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.DAWN_WARRIOR_1) || job.isA(MapleJob.ARAN1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (!usedAPReset) {
               MaxMP += (Randomizer.rand(2, 4) + (player.getInt() / 10));
            } else {
               MaxMP += 2;
            }
         } else {
            MaxMP += 3;
         }
      } else if (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.BLAZE_WIZARD_1)) {
         if (!usedAPReset) {
            int skillId = job.isA(MapleJob.BLAZE_WIZARD_1) ? BlazeWizard.INCREASING_MAX_MP : Magician.IMPROVED_MAX_MP_INCREASE;
            MaxMP += SkillFactory.applyIfHasSkill(player, skillId, (skill, skillLevel) -> skill.getEffect(skillLevel).getY(), 0);
         }

         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (!usedAPReset) {
               MaxMP += (Randomizer.rand(12, 16) + (player.getInt() / 20));
            } else {
               MaxMP += 18;
            }
         } else {
            MaxMP += 18;
         }
      } else if (job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.WIND_ARCHER_1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (!usedAPReset) {
               MaxMP += (Randomizer.rand(6, 8) + (player.getInt() / 10));
            } else {
               MaxMP += 10;
            }
         } else {
            MaxMP += 10;
         }
      } else if (job.isA(MapleJob.THIEF) || job.isA(MapleJob.NIGHT_WALKER_1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (!usedAPReset) {
               MaxMP += (Randomizer.rand(6, 8) + (player.getInt() / 10));
            } else {
               MaxMP += 10;
            }
         } else {
            MaxMP += 10;
         }
      } else if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDER_BREAKER_1)) {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (!usedAPReset) {
               MaxMP += (Randomizer.rand(7, 9) + (player.getInt() / 10));
            } else {
               MaxMP += 14;
            }
         } else {
            MaxMP += 14;
         }
      } else {
         if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
            if (!usedAPReset) {
               MaxMP += (Randomizer.rand(4, 6) + (player.getInt() / 10));
            } else {
               MaxMP += 6;
            }
         } else {
            MaxMP += 6;
         }
      }

      return MaxMP;
   }

   private static int takeHp(MapleJob job) {
      int MaxHP = 0;

      if (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.DAWN_WARRIOR_1) || job.isA(MapleJob.ARAN1)) {
         MaxHP += 54;
      } else if (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.BLAZE_WIZARD_1)) {
         MaxHP += 10;
      } else if (job.isA(MapleJob.THIEF) || job.isA(MapleJob.NIGHT_WALKER_1)) {
         MaxHP += 20;
      } else if (job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.WIND_ARCHER_1)) {
         MaxHP += 20;
      } else if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDER_BREAKER_1)) {
         MaxHP += 42;
      } else {
         MaxHP += 12;
      }

      return MaxHP;
   }

   private static int takeMp(MapleJob job) {
      int MaxMP = 0;

      if (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.DAWN_WARRIOR_1) || job.isA(MapleJob.ARAN1)) {
         MaxMP += 4;
      } else if (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.BLAZE_WIZARD_1)) {
         MaxMP += 31;
      } else if (job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.WIND_ARCHER_1)) {
         MaxMP += 12;
      } else if (job.isA(MapleJob.THIEF) || job.isA(MapleJob.NIGHT_WALKER_1)) {
         MaxMP += 12;
      } else if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDER_BREAKER_1)) {
         MaxMP += 16;
      } else {
         MaxMP += 8;
      }

      return MaxMP;
   }

}
