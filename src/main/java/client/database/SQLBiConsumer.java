package client.database;

import java.sql.SQLException;

public interface SQLBiConsumer<T, U> {
   void accept(T t, U u) throws SQLException;
}
