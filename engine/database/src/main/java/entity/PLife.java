package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "plife")
public class PLife implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer world = -1;

   @Column(nullable = false)
   private Integer map;

   @Column(nullable = false)
   private Integer life;

   @Column(nullable = false)
   private String type = "n";

   @Column(nullable = false)
   private Integer cy;

   @Column(nullable = false)
   private Integer f;

   @Column(nullable = false)
   private Integer fh;

   @Column(nullable = false)
   private Integer rx0;

   @Column(nullable = false)
   private Integer rx1;

   @Column(nullable = false)
   private Integer x;

   @Column(nullable = false)
   private Integer y;

   @Column(nullable = false)
   private Integer hide;

   @Column(nullable = false)
   private Integer mobTime;

   @Column(nullable = false)
   private Integer team;

   public PLife() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
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

   public Integer getLife() {
      return life;
   }

   public void setLife(Integer life) {
      this.life = life;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Integer getCy() {
      return cy;
   }

   public void setCy(Integer cy) {
      this.cy = cy;
   }

   public Integer getF() {
      return f;
   }

   public void setF(Integer f) {
      this.f = f;
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

   public Integer getX() {
      return x;
   }

   public void setX(Integer x) {
      this.x = x;
   }

   public Integer getY() {
      return y;
   }

   public void setY(Integer y) {
      this.y = y;
   }

   public Integer getHide() {
      return hide;
   }

   public void setHide(Integer hide) {
      this.hide = hide;
   }

   public Integer getMobTime() {
      return mobTime;
   }

   public void setMobTime(Integer mobTime) {
      this.mobTime = mobTime;
   }

   public Integer getTeam() {
      return team;
   }

   public void setTeam(Integer team) {
      this.team = team;
   }
}
