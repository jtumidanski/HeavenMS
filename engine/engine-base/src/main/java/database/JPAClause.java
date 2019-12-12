package database;

public class JPAClause {
   private String parameter;

   private String conditional;

   private Object value;

   public JPAClause(String parameter, String conditional, Object value) {
      this.parameter = parameter;
      this.conditional = conditional;
      this.value = value;
   }

   public String getParameter() {
      return parameter;
   }

   public String getConditional() {
      return conditional;
   }

   public Object getValue() {
      return value;
   }
}
