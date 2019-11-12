package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "playernpcs")
public class PlayerNpc implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private Integer hair;

   @Column(nullable = false)
   private Integer face;

   @Column(nullable = false)
   private Integer skin;

   @Column(nullable = false)
   private Integer gender;

   @Column(nullable = false)
   private Integer x;

   @Column(nullable = false)
   private Integer cy;

   @Column(nullable = false)
   private Integer world;

   @Column(nullable = false)
   private Integer map;

   @Column(nullable = false)
   private Integer dir;

   @Column(nullable = false)
   private Integer scriptId;

   @Column(nullable = false)
   private Integer fh;

   @Column(nullable = false)
   private Integer rx0;

   @Column(nullable = false)
   private Integer rx1;

   @Column(nullable = false)
   private Integer worldRank;

   @Column(nullable = false)
   private Integer overallRank;

   @Column(nullable = false)
   private Integer worldJobRank;

   @Column(nullable = false)
   private Integer job;

   public PlayerNpc() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
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

   public Integer getSkin() {
      return skin;
   }

   public void setSkin(Integer skin) {
      this.skin = skin;
   }

   public Integer getGender() {
      return gender;
   }

   public void setGender(Integer gender) {
      this.gender = gender;
   }

   public Integer getX() {
      return x;
   }

   public void setX(Integer x) {
      this.x = x;
   }

   public Integer getCy() {
      return cy;
   }

   public void setCy(Integer cy) {
      this.cy = cy;
   }

   public Integer getWorld() {
      return world;
   }

   public void setWorld(Integer world) {
      this.world = world;
   }

   public Integer getMap() {
      return map;
   }

   public void setMap(Integer map) {
      this.map = map;
   }

   public Integer getDir() {
      return dir;
   }

   public void setDir(Integer dir) {
      this.dir = dir;
   }

   public Integer getScriptId() {
      return scriptId;
   }

   public void setScriptId(Integer scriptId) {
      this.scriptId = scriptId;
   }

   public Integer getFh() {
      return fh;
   }

   public void setFh(Integer fh) {
      this.fh = fh;
   }

   public Integer getRx0() {
      return rx0;
   }

   public void setRx0(Integer rx0) {
      this.rx0 = rx0;
   }

   public Integer getRx1() {
      return rx1;
   }

   public void setRx1(Integer rx1) {
      this.rx1 = rx1;
   }

   public Integer getWorldRank() {
      return worldRank;
   }

   public void setWorldRank(Integer worldRank) {
      this.worldRank = worldRank;
   }

   public Integer getOverallRank() {
      return overallRank;
   }

   public void setOverallRank(Integer overallRank) {
      this.overallRank = overallRank;
   }

   public Integer getWorldJobRank() {
      return worldJobRank;
   }

   public void setWorldJobRank(Integer worldJobRank) {
      this.worldJobRank = worldJobRank;
   }

   public Integer getJob() {
      return job;
   }

   public void setJob(Integer job) {
      this.job = job;
   }
}
