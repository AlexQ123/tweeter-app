package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoUserDAO extends DynamoDAO implements UserDAO {

    private static final String TableName = "user";

    private static final DynamoDbTable<UserBean> userTable = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));

    @Override
    public User getUser(String username) {
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserBean userBean = userTable.getItem(
                (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));

        if (userBean == null) {
            return null;
        }

        return new User(userBean.getFirst_name(), userBean.getLast_name(), userBean.getUser_alias(), userBean.getImage());
    }

    @Override
    public User register(RegisterRequest request) {
        UserBean user = new UserBean();
        user.setUser_alias(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirst_name(request.getFirstName());
        user.setLast_name(request.getLastName());
        user.setImage(request.getImage());

        userTable.putItem(user);

        return new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());
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
