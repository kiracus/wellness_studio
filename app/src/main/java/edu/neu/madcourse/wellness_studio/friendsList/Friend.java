package edu.neu.madcourse.wellness_studio.friendsList;


public class Friend {
    private final String friendUsername;
    private final Integer friendRank;

    public Friend(String name, Integer rank) {
        this.friendUsername = name;
        this.friendRank = rank;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public Integer getFriendRank() {
        return friendRank;
    }
}
