package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.view.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {

    private MainView mockView;
    private StatusService mockStatusService;

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        // Create mocks
        mockView = Mockito.mock(MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        // Posting a status is an authenticated task
        Cache.getInstance().setCurrUserAuthToken(new AuthToken());
    }

    @Test
    public void testPostStatus_postStatusSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthToken authToken = invocation.getArgument(0, AuthToken.class);
                Assertions.assertNotNull(authToken);

                Status status = invocation.getArgument(1, Status.class);
                Assertions.assertEquals(status.getPost(), "test");

                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                Assertions.assertNotNull(observer);
                observer.handleSuccess();

                return null;
            }
        };

        runPostStatus(answer);
        verifyResult(1, "Successfully Posted!");
    }

    @Test
    public void testPostStatus_postStatusFailed() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthToken authToken = invocation.getArgument(0, AuthToken.class);
                Assertions.assertNotNull(authToken);

                Status status = invocation.getArgument(1, Status.class);
                Assertions.assertEquals(status.getPost(), "test");

                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                Assertions.assertNotNull(observer);
                observer.displayErrorMessage("something bad happened");

                return null;
            }
        };

        runPostStatus(answer);
        verifyResult(0, "Failed to post status: something bad happened");
    }

    @Test
    public void testPostStatus_postStatusFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthToken authToken = invocation.getArgument(0, AuthToken.class);
                Assertions.assertNotNull(authToken);

                Status status = invocation.getArgument(1, Status.class);
                Assertions.assertEquals(status.getPost(), "test");

                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                Assertions.assertNotNull(observer);
                observer.displayException(new Exception("an exception happened"));

                return null;
            }
        };

        runPostStatus(answer);
        verifyResult(0, "Failed to post status because of exception: an exception happened");
    }

    private void runPostStatus(Answer answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("test");
    }

    private void verifyResult(Integer times, String s) {
        Mockito.verify(mockView, Mockito.times(times)).cancelPostingToast();
        Mockito.verify(mockView).displayMessage(s);
    }

}
