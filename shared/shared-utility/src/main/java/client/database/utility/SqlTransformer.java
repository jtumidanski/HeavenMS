package client.database.utility;

public interface SqlTransformer<T, U> {
   T transform(U u);
}
