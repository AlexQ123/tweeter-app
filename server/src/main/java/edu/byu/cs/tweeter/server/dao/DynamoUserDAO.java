package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoUserDAO extends DynamoDAO implements UserDAO {

    private static final String TableName = "user";

    private static final DynamoDbTable<UserBean> userTable = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));

    @Override
    public User getUser(GetUserRequest request) {
        return null;
    }

    @Override
    public User login(LoginRequest request) {
        Key key = Key.builder()
                .partitionValue(request.getUsername())
                .build();

        UserBean userBean = userTable.getItem(
                (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));

        return new User(userBean.getFirst_name(), userBean.getLast_name(), userBean.getUser_alias(), userBean.getImage());
    }

    @Override
    public User register(RegisterRequest request) {
        try {
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

        return new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());
    }

    @Override
    public boolean findUser(String username) {
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        return userTable.getItem(
                (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key)) != null;
    }

    @Override
    public boolean validPassword(String username, String password) {
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        return userTable.getItem(
                (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key)).getPassword().equals(password);
    }

}
