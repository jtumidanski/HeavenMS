package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "characters", indexes = {
      @Index(name = "accountId", columnList = "accountId"),
      @Index(name = "party", columnList = "party"),
      @Index(name = "ranking1", columnList = "level,exp"),
      @Index(name = "ranking2", columnList = "gm,job"),
      @Index(name = "id_accountId_world", columnList = "id,accountId,world"),
      @Index(name = "id_accountId_name", columnList = "id,accountId,name")
})
public class Character implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer accountId;

   @Column(nullable = false)
   private Integer world;

   @Column(nullable = false, length = 13)
   private String name;

   @Column(nullable = false)
   private Integer level = 1;

   @Column(nullable = false)
   private Integer exp = 0;

   @Column(nullable = false)
   private Integer gachaponExp = 0;

   @Column(nullable = false)
   private Integer str = 12;

   @Column(nullable = false)
   private Integer dex = 5;

   @Column(nullable = false)
   private Integer luk = 4;

   @Column(nullable = false)
   private Integer intelligence = 4;

   @Column(nullable = false)
   private Integer hp = 50;

   @Column(nullable = false)
   private Integer mp = 5;

   @Column(nullable = false)
   private Integer maxHp = 50;

   @Column(nullable = false)
   private Integer maxMp = 5;

   @Column(nullable = false)
   private Integer meso = 0;

   @Column(nullable = false)
   private Integer hpMpUsed = 0;

   @Column(nullable = false)
   private Integer job = 0;

   @Column(nullable = false)
   private Integer skinColor = 0;

   @Column(nullable = false)
   private Integer gender = 0;

   @Column(nullable = false)
   private Integer fame = 0;

   @Column(nullable = false)
   private Integer fquest = 0;

   @Column(nullable = false)
   private Integer hair = 0;

   @Column(nullable = false)
   private Integer face = 0;

   @Column(nullable = false)
   private Integer ap = 0;

   @Column(nullable = false)
   private String sp = "0,0,0,0,0,0,0,0,0,0";

   @Column(nullable = false)
   private Integer map = 0;

   @Column(nullable = false)
   private Integer spawnPoint = 0;

   @Column(nullable = false)
   private Integer gm = 0;

   @Column(nullable = false)
   private Integer party = 0;

   @Column(nullable = false)
   private Integer buddyCapacity = 25;

   @Column(nullable = false)
   private Timestamp createDate;

   @Column(nullable = false)
   private Integer rank = 1;

   @Column(nullable = false)
   private Integer rankMove = 0;

   @Column(nullable = false)
   private Integer jobRank = 1;

   @Column(nullable = false)
   private Integer jobRankMove = 0;

   @Column(nullable = false)
   private Integer guildId = 0;

   @Column(nullable = false)
   private Integer guildRank = 5;

   @Column(nullable = false)
   private Integer messengerId = 0;

   @Column(nullable = false)
   private Integer messengerPosition = 4;

   @Column(nullable = false)
   private Integer mountLevel = 1;

   @Column(nullable = false)
   private Integer mountExp = 0;

   @Column(nullable = false)
   private Integer mountTiredness = 0;

   @Column(nullable = false)
   private Integer omokWins = 0;

   @Column(nullable = false)
   private Integer omokLosses = 0;

   @Column(nullable = false)
   private Integer omokTies = 0;

   @Column(nullable = false)
   private Integer matchCardWins = 0;

   @Column(nullable = false)
   private Integer matchCardLosses = 0;

   @Column(nullable = false)
   private Integer matchCardTies = 0;

   @Column(nullable = false)
   private Integer merchantMesos = 0;

   @Column(nullable = false)
   private Integer hasMerchant = 0;

   @Column(nullable = false)
   private Integer equipSlots = 24;

   @Column(nullable = false)
   private Integer useSlots = 24;

   @Column(nullable = false)
   private Integer setupSlots = 24;

   @Column(nullable = false)
   private Integer etcSlots = 24;

   @Column(nullable = false)
   private Integer familyId = -1;

   @Column(nullable = false)
   private Integer monsterBookCover = 0;

   @Column(nullable = false)
   private Integer allianceRank = 5;

   @Column(nullable = false)
   private Integer vanquisherStage = 0;

   @Column(nullable = false)
   private Integer ariantPoints = 0;

   @Column(nullable = false)
   private Integer dojoPoints = 0;

   @Column(nullable = false)
   private Integer lastDojoStage = 0;

   @Column(nullable = false)
   private Integer finishedDojoTutorial = 0;

   @Column(nullable = false)
   private Integer vanquisherKills = 0;

   @Column(nullable = false)
   private Integer summonValue = 0;

   @Column(nullable = false)
   private Integer partnerId = 0;

   @Column(nullable = false)
   private Integer marriageItemId = 0;

   @Column(nullable = false)
   private Integer reborns = 0;

   @Column(nullable = false)
   private Integer pqPoints = 0;

   @Column(nullable = false)
   private String dataString;

   @Column(nullable = false)
   private Timestamp lastLogoutTime;

   @Column(nullable = false)
   private Timestamp lastExpGainTime;

   @Column(nullable = false)
   private Integer partySearch;

   @Column(nullable = false)
   private Long jailExpire = 0L;

   public Character() {
      createDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
      lastLogoutTime = new Timestamp(0);
      lastExpGainTime = new Timestamp(0);
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public Integer getWorld() {
      return world;
   }

   public void setWorld(Integer world) {
      this.world = world;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }

   public Integer getExp() {
      return exp;
   }

   public void setExp(Integer exp) {
      this.exp = exp;
   }

   public Integer getGachaponExp() {
      return gachaponExp;
   }

   public void setGachaponExp(Integer gachaponExp) {
      this.gachaponExp = gachaponExp;
   }

   public Integer getStr() {
      return str;
   }

   public void setStr(Integer str) {
      this.str = str;
   }

   public Integer getDex() {
      return dex;
   }

   public void setDex(Integer dex) {
      this.dex = dex;
   }

   public Integer getLuk() {
      return luk;
   }

   public void setLuk(Integer luk) {
      this.luk = luk;
   }

   public Integer getIntelligence() {
      return intelligence;
   }

   public void setIntelligence(Integer intelligence) {
      this.intelligence = intelligence;
   }

   public Integer getHp() {
      return hp;
   }

   public void setHp(Integer hp) {
      this.hp = hp;
   }

   public Integer getMp() {
      return mp;
   }

   public void setMp(Integer mp) {
      this.mp = mp;
   }

   public Integer getMaxHp() {
      return maxHp;
   }

   public void setMaxHp(Integer maxHp) {
      this.maxHp = maxHp;
   }

   public Integer getMaxMp() {
      return maxMp;
   }

   public void setMaxMp(Integer maxMp) {
      this.maxMp = maxMp;
   }

   public Integer getMeso() {
      return meso;
   }

   public void setMeso(Integer meso) {
      this.meso = meso;
   }

   public Integer getHpMpUsed() {
      return hpMpUsed;
   }

   public void setHpMpUsed(Integer hpMpUsed) {
      this.hpMpUsed = hpMpUsed;
   }

   public Integer getJob() {
      return job;
   }

   public void setJob(Integer job) {
      this.job = job;
   }

   public Integer getSkinColor() {
      return skinColor;
   }

   public void setSkinColor(Integer skinColor) {
      this.skinColor = skinColor;
   }

   public Integer getGender() {
      return gender;
   }

   public void setGender(Integer gender) {
      this.gender = gender;
   }

   public Integer getFame() {
      return fame;
   }

   public void setFame(Integer fame) {
      this.fame = fame;
   }

   public Integer getFquest() {
      return fquest;
   }

   public void setFquest(Integer fquest) {
      this.fquest = fquest;
   }

   public Integer getHair() {
      return hair;
   }

   public void setHair(Integer hair) {
      this.hair = hair;
   }

   public Integer getFace() {
      return face;
   }

   public void setFace(Integer face) {
      this.face = face;
   }

   public Integer getAp() {
      return ap;
   }

   public void setAp(Integer ap) {
      this.ap = ap;
   }

   public String getSp() {
      return sp;
   }

   public void setSp(String sp) {
      this.sp = sp;
   }

   public Integer getMap() {
      return map;
   }

   public void setMap(Integer map) {
      this.map = map;
   }

   public Integer getSpawnPoint() {
      return spawnPoint;
   }

   public void setSpawnPoint(Integer spawnPoint) {
      this.spawnPoint = spawnPoint;
   }

   public Integer getGm() {
      return gm;
   }

   public void setGm(Integer gm) {
      this.gm = gm;
   }

   public Integer getParty() {
      return party;
   }

   public void setParty(Integer party) {
      this.party = party;
   }

   public Integer getBuddyCapacity() {
      return buddyCapacity;
   }

   public void setBuddyCapacity(Integer buddyCapacity) {
      this.buddyCapacity = buddyCapacity;
   }

   public Timestamp getCreateDate() {
      return createDate;
   }

   public void setCreateDate(Timestamp createDate) {
      this.createDate = createDate;
   }

   public Integer getRank() {
      return rank;
   }

   public void setRank(Integer rank) {
      this.rank = rank;
   }

   public Integer getRankMove() {
      return rankMove;
   }

   public void setRankMove(Integer rankMove) {
      this.rankMove = rankMove;
   }

   public Integer getJobRank() {
      return jobRank;
   }

   public void setJobRank(Integer jobRank) {
      this.jobRank = jobRank;
   }

   public Integer getJobRankMove() {
      return jobRankMove;
   }

   public void setJobRankMove(Integer jobRankMove) {
      this.jobRankMove = jobRankMove;
   }

   public Integer getGuildId() {
      return guildId;
   }

   public void setGuildId(Integer guildId) {
      this.guildId = guildId;
   }

   public Integer getGuildRank() {
      return guildRank;
   }

   public void setGuildRank(Integer guildRank) {
      this.guildRank = guildRank;
   }

   public Integer getMessengerId() {
      return messengerId;
   }

   public void setMessengerId(Integer messengerId) {
      this.messengerId = messengerId;
   }

   public Integer getMessengerPosition() {
      return messengerPosition;
   }

   public void setMessengerPosition(Integer messengerPosition) {
      this.messengerPosition = messengerPosition;
   }

   public Integer getMountLevel() {
      return mountLevel;
   }

   public void setMountLevel(Integer mountLevel) {
      this.mountLevel = mountLevel;
   }

   public Integer getMountExp() {
      return mountExp;
   }

   public void setMountExp(Integer mountExp) {
      this.mountExp = mountExp;
   }

   public Integer getMountTiredness() {
      return mountTiredness;
   }

   public void setMountTiredness(Integer mountTiredness) {
      this.mountTiredness = mountTiredness;
   }

   public Integer getOmokWins() {
      return omokWins;
   }

   public void setOmokWins(Integer omokWins) {
      this.omokWins = omokWins;
   }

   public Integer getOmokLosses() {
      return omokLosses;
   }

   public void setOmokLosses(Integer omokLosses) {
      this.omokLosses = omokLosses;
   }

   public Integer getOmokTies() {
      return omokTies;
   }

   public void setOmokTies(Integer omokTies) {
      this.omokTies = omokTies;
   }

   public Integer getMatchCardWins() {
      return matchCardWins;
   }

   public void setMatchCardWins(Integer matchCardWins) {
      this.matchCardWins = matchCardWins;
   }

   public Integer getMatchCardLosses() {
      return matchCardLosses;
   }

   public void setMatchCardLosses(Integer matchCardLosses) {
      this.matchCardLosses = matchCardLosses;
   }

   public Integer getMatchCardTies() {
      return matchCardTies;
   }

   public void setMatchCardTies(Integer matchCardTies) {
      this.matchCardTies = matchCardTies;
   }

   public Integer getMerchantMesos() {
      return merchantMesos;
   }

   public void setMerchantMesos(Integer merchantMesos) {
      this.merchantMesos = merchantMesos;
   }

   public Integer getHasMerchant() {
      return hasMerchant;
   }

   public void setHasMerchant(Integer hasMerchant) {
      this.hasMerchant = hasMerchant;
   }

   public Integer getEquipSlots() {
      return equipSlots;
   }

   public void setEquipSlots(Integer equipSlots) {
      this.equipSlots = equipSlots;
   }

   public Integer getUseSlots() {
      return useSlots;
   }

   public void setUseSlots(Integer useSlots) {
      this.useSlots = useSlots;
   }

   public Integer getSetupSlots() {
      return setupSlots;
   }

   public void setSetupSlots(Integer setupSlots) {
      this.setupSlots = setupSlots;
   }

   public Integer getEtcSlots() {
      return etcSlots;
   }

   public void setEtcSlots(Integer etcSlots) {
      this.etcSlots = etcSlots;
   }

   public Integer getFamilyId() {
      return familyId;
   }

   public void setFamilyId(Integer familyId) {
      this.familyId = familyId;
   }

   public Integer getMonsterBookCover() {
      return monsterBookCover;
   }

   public void setMonsterBookCover(Integer monsterBookCover) {
      this.monsterBookCover = monsterBookCover;
   }

   public Integer getAllianceRank() {
      return allianceRank;
   }

   public void setAllianceRank(Integer allianceRank) {
      this.allianceRank = allianceRank;
   }

   public Integer getVanquisherStage() {
      return vanquisherStage;
   }

   public void setVanquisherStage(Integer vanquisherStage) {
      this.vanquisherStage = vanquisherStage;
   }

   public Integer getAriantPoints() {
      return ariantPoints;
   }

   public void setAriantPoints(Integer ariantPoints) {
      this.ariantPoints = ariantPoints;
   }

   public Integer getDojoPoints() {
      return dojoPoints;
   }

   public void setDojoPoints(Integer dojoPoints) {
      this.dojoPoints = dojoPoints;
   }

   public Integer getLastDojoStage() {
      return lastDojoStage;
   }

   public void setLastDojoStage(Integer lastDojoStage) {
      this.lastDojoStage = lastDojoStage;
   }

   public Integer getFinishedDojoTutorial() {
      return finishedDojoTutorial;
   }

   public void setFinishedDojoTutorial(Integer finishedDojoTutorial) {
      this.finishedDojoTutorial = finishedDojoTutorial;
   }

   public Integer getVanquisherKills() {
      return vanquisherKills;
   }

   public void setVanquisherKills(Integer vanquisherKills) {
      this.vanquisherKills = vanquisherKills;
   }

   public Integer getSummonValue() {
      return summonValue;
   }

   public void setSummonValue(Integer summonValue) {
      this.summonValue = summonValue;
   }

   public Integer getPartnerId() {
      return partnerId;
   }

   public void setPartnerId(Integer partnerId) {
      this.partnerId = partnerId;
   }

   public Integer getMarriageItemId() {
      return marriageItemId;
   }

   public void setMarriageItemId(Integer marriageItemId) {
      this.marriageItemId = marriageItemId;
   }

   public Integer getReborns() {
      return reborns;
   }

   public void setReborns(Integer reborns) {
      this.reborns = reborns;
   }

   public Integer getPqPoints() {
      return pqPoints;
   }

   public void setPqPoints(Integer pqPoints) {
      this.pqPoints = pqPoints;
   }

   public String getDataString() {
      return dataString;
   }

   public void setDataString(String dataString) {
      this.dataString = dataString;
   }

   public Timestamp getLastLogoutTime() {
      return lastLogoutTime;
   }

   public void setLastLogoutTime(Timestamp lastLogoutTime) {
      this.lastLogoutTime = lastLogoutTime;
   }

   public Timestamp getLastExpGainTime() {
      return lastExpGainTime;
   }

   public void setLastExpGainTime(Timestamp lastExpGainTime) {
      this.lastExpGainTime = lastExpGainTime;
   }

   public Integer getPartySearch() {
      return partySearch;
   }

   public void setPartySearch(Integer partySearch) {
      this.partySearch = partySearch;
   }

   public Long getJailExpire() {
      return jailExpire;
   }

   public void setJailExpire(Long jailExpire) {
      this.jailExpire = jailExpire;
   }
}
