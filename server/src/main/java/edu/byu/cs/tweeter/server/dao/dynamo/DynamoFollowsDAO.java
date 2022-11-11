package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.bean.FollowsBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoFollowsDAO extends DynamoDAO implements FollowsDAO {

    private static final String TableName = "follows";

    private static final DynamoDbTable<FollowsBean> followsTable = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class));

    private final UserDAO userDAO = new DynamoUserDAO();

    @Override
    public Pair<List<User>, Boolean> getPagedFollowees(String followerHandle, int pageSize, String lastFollowee) {
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
    public Pair<List<User>, Boolean> getPagedFollowers(String followeeHandle, int pageSize, String lastFollower) {
        List<FollowsBean> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();
        boolean hasMorePages = false;

        try {
            DynamoDbIndex<FollowsBean> followsIndex = enhancedClient.table("follows", TableSchema.fromBean(FollowsBean.class))
                    .index("follows_index");

            Key key = Key.builder()
                    .partitionValue(followeeHandle)
                    .build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).limit(pageSize+1);

            if (isNonEmptyString(lastFollower)) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("followee_handle", AttributeValue.builder().s(followeeHandle).build());
                startKey.put("follower_handle", AttributeValue.builder().s(lastFollower).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest request = requestBuilder.build();

            SdkIterable<Page<FollowsBean>> results = followsIndex.query(request);
            PageIterable<FollowsBean> pages = PageIterable.create(results);

            pages.stream()
                    .limit(1)
                    .forEach(followsPage -> followsPage.items().forEach(f -> followers.add(f)));

            // Check if there's more pages
            if (followers.size() == pageSize + 1) {
                hasMorePages = true;
                followers.remove(followers.size() - 1);
            }

            // Convert FollowsBean to users
            for (FollowsBean follower : followers) {
                User converted = userDAO.getUser(follower.getFollower_handle());
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
    public boolean checkIsFollower(String followerAlias, String followeeAlias) {
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        FollowsBean followsBean = followsTable.getItem(
                (GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));

        return followsBean != null;
    }

    @Override
    public void deleteFollows(String followerAlias, String followeeAlias) {
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        followsTable.deleteItem(key);
    }

    @Override
    public void addFollows(User follower, User followee) {
        FollowsBean followsBean = new FollowsBean();
        followsBean.setFollower_handle(follower.getAlias());
        followsBean.setFollower_name(follower.getFirstName() + " " + follower.getLastName());
        followsBean.setFollowee_handle(followee.getAlias());
        followsBean.setFollowee_name(followee.getFirstName() + " " + followee.getLastName());

        followsTable.putItem(followsBean);
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

}
