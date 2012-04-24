package com.googlecode.ounit.quercus;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceDecorator implements DataSource {
	DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return new ConnectionDecorator(dataSource.getConnection());
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return new ConnectionDecorator(dataSource.getConnection(username, password));
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return dataSource.getLogWriter();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return dataSource.getLoginTimeout();
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return dataSource.isWrapperFor(arg0);
	}

	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
		dataSource.setLogWriter(arg0);
	}

	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
		dataSource.setLoginTimeout(arg0);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return dataSource.unwrap(arg0);
	}
}