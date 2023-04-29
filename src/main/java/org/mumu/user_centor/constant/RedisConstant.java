package org.mumu.user_centor.constant;

public class RedisConstant {
    public static final String RECOMMEND = "match:user:recommend:";
    public static final String LOCK = "match:precache:lock";
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;
    public static final String FEED_KEY = "feed:";
    public static final String POST_LIKED_KEY = "post:liked:";
    public static final String FOLLOW_KEY = "follows:";
    public static final String USER_GEO_KEY = "geo:user";
}
