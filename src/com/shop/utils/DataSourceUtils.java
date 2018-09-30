package com.shop.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceUtils {
	private static DataSource dataSource = new ComboPooledDataSource();
	private static ThreadLocal<Connection> tLocal = new ThreadLocal<>();
	
	
	public static DataSource getDataSource() {
		return dataSource;
	}
	
	public static Connection getConnection() throws SQLException {
		Connection connection = tLocal.get();
		if (connection == null){
			connection = dataSource.getConnection();
			tLocal.set(connection);
		}
		return connection;
	}
	
	public static void startTransaction() throws SQLException {
		Connection connection = getConnection();
		if (connection != null) {
			connection.setAutoCommit(true);
		}
	}
	
	public static void rollback() throws SQLException {
		Connection connection = getConnection();
		if (connection != null){
			connection.rollback();
		}
	}
	
	public static void commitAndRelease() throws SQLException {
		Connection connection = getConnection();
		if (connection != null) {
			connection.commit();
			connection.close();
			tLocal.remove();
		}
	}
	
	public static void closeConnection() throws SQLException {
		Connection connection = getConnection();
		if (connection != null) {
			connection.close();
		}
	}
	
	public static void closeStatement(Statement st) throws SQLException {
		if (st != null) {
			st.close();
		}
	}
	
	public static void closeResultSet(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}
}
