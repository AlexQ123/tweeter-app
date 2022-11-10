package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements DAOFactory {

    @Override
    public UserDAO createUserDAO() {
        return new DynamoUserDAO();
    }

    @Override
    public ImageDAO createImageDAO() {
        return new S3ImageDAO();
    }

    @Override
    public AuthTokenDAO createAuthTokenDAO() {
        return new DynamoAuthTokenDAO();
    }

}
