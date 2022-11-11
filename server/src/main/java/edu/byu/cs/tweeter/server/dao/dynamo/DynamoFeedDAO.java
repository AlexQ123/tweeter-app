package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoFeedDAO extends DynamoDAO implements FeedDAO {

    private static final String TableName = "feed";

    private static final DynamoDbTable<FeedBean> feedTable = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));

    private final FollowsDAO followsDAO = new DynamoFollowsDAO();

    @Override
    public Pair<List<Status>, Boolean> getPagedFeed(String targetUserAlias, int pageSize, Status lastStatus) {
        return null;
    }

    @Override
    public void addStatus(Status status) {
        User sender = status.getUser();
        List<User> receivers = followsDAO.getAllFollowers(sender.getAlias());

        for (User receiver : receivers) {
            FeedBean feedBean = new FeedBean();

            feedBean.setReceiver_alias(receiver.getAlias());
            feedBean.setTimestamp(status.getTimestamp());
            feedBean.setFormattedDateTime(status.getDatetime());
            feedBean.setPost(status.getPost());
            feedBean.setFirst_name(sender.getFirstName());
            feedBean.setLast_name(sender.getLastName());
            feedBean.setImage(sender.getImageUrl());

            feedTable.putItem(feedBean);
        }
    }

}
