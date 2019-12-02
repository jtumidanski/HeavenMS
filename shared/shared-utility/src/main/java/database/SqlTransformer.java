package database;

public interface SqlTransformer<T, U> {
   T transform(U u);
}
