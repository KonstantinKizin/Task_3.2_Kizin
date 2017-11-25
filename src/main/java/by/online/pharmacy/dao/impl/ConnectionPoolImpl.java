package by.online.pharmacy.dao.impl;

import by.online.pharmacy.dao.ConnectionPool;
import by.online.pharmacy.dao.exception.ConnectionPoolException;
import by.online.pharmacy.dao.exception.DAOException;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import static by.online.pharmacy.service.impl.PropertyLoader.getConstant;
import static by.online.pharmacy.entity.constant.PropertyEnum.DateBaseProperty;

public final class ConnectionPoolImpl implements ConnectionPool {

    private final int CONNECTION_POOL_SIZE = 20;
    private Vector<Connection> availableConnections;
    private Vector<Connection> usedConnections;
    private final static Logger lOGGER = Logger.getLogger(ConnectionPoolImpl.class);

    private static final ConnectionPool instance = new ConnectionPoolImpl();



    private ConnectionPoolImpl()  {
        try {
            Class.forName(getConstant(DateBaseProperty.DB_DRIVER_NAME.name()));
        } catch (ClassNotFoundException e) {
           lOGGER.error("JDBC driver not found",new DAOException(e));
           throw new RuntimeException(e);
        }

        availableConnections = new Vector<>(CONNECTION_POOL_SIZE);
        usedConnections = new Vector<>(CONNECTION_POOL_SIZE);
        makeConnectionsQueue(CONNECTION_POOL_SIZE);
    }


    public static ConnectionPool getInstance(){
        return instance;
    }


    @Override
    public Connection getConnection() throws ConnectionPoolException {

        Connection connection = null;
        if(availableConnections.isEmpty()){
            connection = createConnection();
            return connection;
        }else {
            connection = availableConnections.firstElement();
            usedConnections.add(connection);
            availableConnections.remove(connection);
            return connection;
        }
    }



    @Override
    public boolean roleBack(Connection connection) {
        usedConnections.remove(connection);
        return availableConnections.add(connection);
    }



    private void makeConnectionsQueue(int count)  {
        for(int i = 0; i < count;i++){
            Connection connection = null;
            try {
                connection = createConnection();
            } catch (ConnectionPoolException e) {
                lOGGER.error("Exception in make connections method",e);
                throw new RuntimeException(e);
            }
            availableConnections.add(connection);
        }
    }


    private Connection  createConnection() throws ConnectionPoolException {
        try {
            Connection connection = DriverManager.getConnection(
                    getConstant(DateBaseProperty.DB_URL.name()),
                    getConstant(DateBaseProperty.DB_LOGIN.name()),
                    getConstant(DateBaseProperty.DB_PASSWORD.name())
            );
            return connection;
        } catch (SQLException  e) {
            lOGGER.error("create Connection exception ",e);
            throw new ConnectionPoolException(e);
        }
    }
}
