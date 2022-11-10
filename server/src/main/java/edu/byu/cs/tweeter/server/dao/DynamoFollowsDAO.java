package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.bean.FollowsBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoFollowsDAO extends DynamoDAO implements FollowsDAO {

    private static final String TableName = "follows";

    private static final DynamoDbTable<FollowsBean> followsTable = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class));

    private final UserDAO userDAO = new DynamoUserDAO();

    @Override
    public Pair<List<User>, Boolean> getFollowees(String followerHandle, int pageSize, String lastFollowee) {
        List<FollowsBean> followees = new ArrayList<>();
        List<User> users = new ArrayList<>();
        boolean hasMorePages = false;

        try {
            Key key = Key.builder()
                    .partitionValue(followerHandle)
                    .build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

            if (isNonEmptyString(lastFollowee)) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follower_handle", AttributeValue.builder().s(followerHandle).build());
                startKey.put("followee_handle", AttributeValue.builder().s(lastFollowee).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest request = requestBuilder.build();

            followsTable.query(request).items().stream().limit(pageSize+1).forEach(f -> followees.add(f));

            // Check if there's more pages
            if (followees.size() == pageSize + 1) {
                hasMorePages = true;
                followees.remove(followees.size() - 1);
            }

            // Convert FollowsBean to users
            for (FollowsBean followee : followees) {
                User converted = userDAO.getUser(followee.getFollowee_handle());
                users.add(converted);
            }

        }
        catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return new Pair<>(users, hasMorePages);
    }

    @Override
    public List<User> getFollowers(String followeeHandle, int pageSize, String lastFollower) {
        return null;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

}
