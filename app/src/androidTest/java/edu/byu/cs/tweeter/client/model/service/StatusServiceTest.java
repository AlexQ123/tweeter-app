package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceTest {

    private User currentUser;
    private AuthToken currentAuthToken;

    private StatusService statusServiceSpy;
    private StatusServiceObserver observer;
    private PagedPresenter<Status> mockPresenter;

    private CountDownLatch countDownLatch;

    @BeforeEach
    public void setup() {
        currentUser = new User("FirstName", "LastName", null);
        currentAuthToken = new AuthToken();

        statusServiceSpy = Mockito.spy(new StatusService());
        mockPresenter = Mockito.mock(PagedPresenter.class);
        observer = new StatusServiceObserver(mockPresenter);

        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class StatusServiceObserver extends PagedObserver<Status> {
        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        public StatusServiceObserver(PagedPresenter<Status> presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = items;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        protected void showError(String message) {
            countDownLatch.countDown();
        }

        @Override
        protected void showException(Exception ex) {
            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    @Test
    public void loadMoreStorySuccess() throws InterruptedException {
        statusServiceSpy.loadMoreStory(currentAuthToken, currentUser, 3, null, observer);
        awaitCountDownLatch();

        List<Status> expectedStatuses = FakeData.getInstance().getFakeStatuses().subList(0, 3);

        Assertions.assertTrue(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());

        // The datetime of the statuses will be different because AWS is in a different time zone
        // than my local machine. Thus, I check for everything except datetime.
        Assertions.assertEquals(expectedStatuses.size(), observer.getStatuses().size());
        for (int i = 0; i < expectedStatuses.size(); i++) {
            Assertions.assertEquals(expectedStatuses.get(i).getPost(), observer.getStatuses().get(i).getPost());
            Assertions.assertEquals(expectedStatuses.get(i).getUser(), observer.getStatuses().get(i).getUser());
            Assertions.assertEquals(expectedStatuses.get(i).getMentions(), observer.getStatuses().get(i).getMentions());
            Assertions.assertEquals(expectedStatuses.get(i).getUrls(), observer.getStatuses().get(i).getUrls());
        }

        Assertions.assertTrue(observer.getHasMorePages());
        Assertions.assertNull(observer.getException());
    }

}
