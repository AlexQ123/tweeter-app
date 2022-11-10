package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class Service {

    protected final UserDAO userDAO;
    protected final ImageDAO imageDAO;
    protected final AuthTokenDAO authTokenDAO;

    public Service (DAOFactory daoFactory) {
        this.userDAO = daoFactory.createUserDAO();
        this.imageDAO = daoFactory.createImageDAO();
        this.authTokenDAO = daoFactory.createAuthTokenDAO();
    }

}
