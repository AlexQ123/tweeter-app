package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoUserDAO extends DynamoDAO implements UserDAO {

    private static final String TableName = "user";

    private static final String HandleAttr = "user_alias";
    private static final String PasswordAttr = "password";
    private static final String FirstNameAttr = "first_name";
    private static final String LastNameAttr = "last_name";
    private static final String ImageAttr = "image";

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        return null;
    }

    @Override
    public AuthenticateResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthenticateResponse register(RegisterRequest request) {
        try {
            DynamoDbTable<UserBean> userTable = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));

            UserBean foundUser = findUser(request.getUsername());

            if (foundUser != null) {
                return new AuthenticateResponse("Username is taken, please choose a different one.");
            }

            UserBean user = new UserBean();
            user.setUser_alias(request.getUsername());
            user.setPassword(request.getPassword());
            user.setFirst_name(request.getFirstName());
            user.setLast_name(request.getLastName());
            user.setImage(request.getImage());

            userTable.putItem(user);
        }
        catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());
        return new AuthenticateResponse(user, new AuthToken("placeholder"));
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return null;
    }

    private UserBean findUser(String username) {
        DynamoDbTable<UserBean> userTable = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));

        Key key = Key.builder()
                .partitionValue(username)
                .build();

        return userTable.getItem(
                (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));
    }

}
