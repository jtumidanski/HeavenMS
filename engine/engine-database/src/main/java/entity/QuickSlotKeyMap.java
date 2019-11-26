package entity;

import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "quick_slot_key_map")
public class QuickSlotKeyMap {

   @Column(nullable = false)
   private Integer id;

   @Column(nullable = false)
   private BigInteger keyMap = BigInteger.ZERO;

   public QuickSlotKeyMap() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public BigInteger getKeyMap() {
      return keyMap;
   }

   public void setKeyMap(BigInteger keyMap) {
      this.keyMap = keyMap;
   }
}
