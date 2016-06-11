package salarycalculation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import salarycalculation.database.model.CapabilityRecord;
import salarycalculation.exception.RecordNotFoundException;
import salarycalculation.exception.RuntimeSQLException;

/**
 * 能力等級 Dao。
 *
 * @author naotake
 */
public class CapabilityDao {

    private Connection connection;

    public CapabilityDao() {
        String url = "jdbc:h2:./data/salary_calculation";
        try {
            this.connection = DriverManager.getConnection(url, "sa", "");
        } catch (SQLException e) {
            throw new RuntimeSQLException("Connection Failure", e);
        }
    }

    /**
     * 等級を基に能力等級を取得する。
     *
     * @param rank 等級
     * @return 能力等力
     */
    public CapabilityRecord get(String rank) {
        ResultSetHandler<CapabilityRecord> rsHandler = new BeanHandler<CapabilityRecord>(CapabilityRecord.class);
        QueryRunner runner = new QueryRunner();

        CapabilityRecord result = null;
        try {
            result = runner.query(connection, "select * from capability where rank = '" + rank
                    + "'", rsHandler);
        } catch (SQLException e) {
            throw new RuntimeSQLException("Select Failure", e);
        }

        if (result == null) {
            throw new RecordNotFoundException(CapabilityRecord.class, rank);
        }

        return result;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
