package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "alliance", indexes = {
      @Index(name = "name", columnList = "name")
})
public class Alliance implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   @Column(nullable = false)
   private Integer id;

   @Column(nullable = false, length = 13)
   private String name;

   @Column(nullable = false)
   private Integer capacity = 2;

   @Column(nullable = false, length = 20)
   private String notice = "";

   @Column(nullable = false, length = 11)
   private String rank1 = "Master";

   @Column(nullable = false, length = 11)
   private String rank2 = "Jr. Master";

   @Column(nullable = false, length = 11)
   private String rank3 = "Member";

   @Column(nullable = false, length = 11)
   private String rank4 = "Member";

   @Column(nullable = false, length = 11)
   private String rank5 = "Member";

   public Alliance() {
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

   public Integer getCapacity() {
      return capacity;
   }

   public void setCapacity(Integer capacity) {
      this.capacity = capacity;
   }

   public String getNotice() {
      return notice;
   }

   public void setNotice(String notice) {
      this.notice = notice;
   }

   public String getRank1() {
      return rank1;
   }

   public void setRank1(String rank1) {
      this.rank1 = rank1;
   }

   public String getRank2() {
      return rank2;
   }

   public void setRank2(String rank2) {
      this.rank2 = rank2;
   }

   public String getRank3() {
      return rank3;
   }

   public void setRank3(String rank3) {
      this.rank3 = rank3;
   }

   public String getRank4() {
      return rank4;
   }

   public void setRank4(String rank4) {
      this.rank4 = rank4;
   }

   public String getRank5() {
      return rank5;
   }

   public void setRank5(String rank5) {
      this.rank5 = rank5;
   }
}
