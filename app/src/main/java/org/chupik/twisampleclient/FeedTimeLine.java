package org.chupik.twisampleclient;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineCursor;
import com.twitter.sdk.android.tweetui.TimelineResult;

import java.util.List;

import retrofit2.Call;

class FeedTimeLine implements Timeline<Tweet> {


    @Override
    public void next(Long minPosition, Callback<TimelineResult<Tweet>> cb) {
        createFeedTimelineRequest(minPosition, null).enqueue(new TweetsCallback(cb));
    }

    @Override
    public void previous(Long maxPosition, Callback<TimelineResult<Tweet>> cb) {
        createFeedTimelineRequest(null, decrementMaxId(maxPosition)).enqueue(new TweetsCallback(cb));
    }

    Call<List<Tweet>> createFeedTimelineRequest(final Long sinceId, final Long maxId) {
        TwitterCore twitterCore = TwitterCore.getInstance();
        return twitterCore.getApiClient().getStatusesService()
                .homeTimeline(null, sinceId, maxId, false, false, true, false);
    }

    Long decrementMaxId(Long maxId) {
        return maxId == null ? null : maxId - 1;
    }

    /**
     * Wrapper callback which unpacks a list of Tweets into a TimelineResult (cursor and items).
     * Copy of {@link com.twitter.sdk.android.tweetui.BaseTimeline.TweetsCallback}
     */
    static class TweetsCallback extends Callback<List<Tweet>> {
        final Callback<TimelineResult<Tweet>> cb;

        TweetsCallback(Callback<TimelineResult<Tweet>> cb) {
            this.cb = cb;
        }

        @Override
        public void success(Result<List<Tweet>> result) {
            final List<Tweet> tweets = result.data;
            final TimelineResult<Tweet> timelineResult
                    = new TimelineResult<>(newTimelineCursor(tweets), tweets);
            if (cb != null) {
                cb.success(new Result<>(timelineResult, result.response));
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }

        private static TimelineCursor newTimelineCursor(List<Tweet> items) {
            Long minPosition = items.size() > 0 ? items.get(items.size() - 1).getId() : null;
            Long maxPosition = items.size() > 0 ? items.get(0).getId() : null;
            return new TimelineCursor(minPosition, maxPosition);
        }
    }
}
