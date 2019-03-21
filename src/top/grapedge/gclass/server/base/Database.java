package top.grapedge.gclass.server.base;

import java.sql.*;

/**
 * @program: G-ClassManager
 * @description: 数据库处理
 * @author: Grapes
 * @create: 2019-03-14 13:27
 **/
public class Database {

    private static Database instance;
    private final String DATABASE = "jdbc:mysql://localhost:3306/g-class-manager?serverTimezone=GMT%2B8";
    private final String USERNAME = "root";
    private final String PASSWORD = "8835230.";

    public static Database getInstance() {
        return instance;
    }

    private Connection conn;
    private Statement stat;
    public Database() throws SQLException {
        instance = this;
        conn = DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
        stat = conn.createStatement();
    }

    public Connection getConn() {
        return conn;
    }

    public Statement getStat() {
        return stat;
    }

    public int update(String sql) throws SQLException {
        return stat.executeUpdate(sql);
    }

    public ResultSet query(String sql) throws SQLException {
        return stat.executeQuery(sql);
    }

    public PreparedStatement preparedStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }
}
