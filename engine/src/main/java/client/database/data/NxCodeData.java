package client.database.data;

public class NxCodeData {
   private String retriever;

   private long expiration;

   private int id;

   public NxCodeData(String retriever, long expiration, int id) {
      this.retriever = retriever;
      this.expiration = expiration;
      this.id = id;
   }

   public String getRetriever() {
      return retriever;
   }

   public long getExpiration() {
      return expiration;
   }

   public int getId() {
      return id;
   }
}
