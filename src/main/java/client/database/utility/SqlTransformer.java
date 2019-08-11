package client.database.utility;

import java.sql.SQLException;

public interface SqlTransformer<T, U> {
   T transform(U u) throws SQLException;
}
