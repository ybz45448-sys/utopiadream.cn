package com.utopia.day23.dto;

public class LikeResponse {

    // 当前用户操作后，这个话题是否处于已点赞状态
    private boolean liked;

    // 这个话题当前的总点赞数
    private long likes;

    public LikeResponse() {
    }

    public LikeResponse(boolean liked, long likes) {
        this.liked = liked;
        this.likes = likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }
}
