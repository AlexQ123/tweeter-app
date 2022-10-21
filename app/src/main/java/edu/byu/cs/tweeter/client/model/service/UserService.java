package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.tasks.handler.AuthenticateHandler;
import edu.byu.cs.tweeter.client.model.service.tasks.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.tasks.handler.UserHandler;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.client.presenter.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.tasks.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.tasks.LoginTask;
import edu.byu.cs.tweeter.client.model.service.tasks.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.tasks.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService {

    public void userClicked(AuthToken currUserAuthToken, String userAlias, UserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken,
                userAlias, new UserHandler(getUserObserver));
        BackgroundTaskUtils.runTask(getUserTask);
        getUserObserver.displayGettingUser();
    }

    public void login(String alias, String password, AuthenticateObserver loginObserver) {
        LoginTask loginTask = new LoginTask(alias, password, new AuthenticateHandler(loginObserver));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, AuthenticateObserver registerObserver) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64,
                new AuthenticateHandler(registerObserver));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void logout(AuthToken currUserAuthToken, SimpleNotificationObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new SimpleNotificationHandler(logoutObserver));
        BackgroundTaskUtils.runTask(logoutTask);
    }

}
