package com.dtmining.latte.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userProfileDaoConfig;

    private final UserProfileDao userProfileDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userProfileDaoConfig = daoConfigMap.get(UserProfileDao.class).clone();
        userProfileDaoConfig.initIdentityScope(type);

        userProfileDao = new UserProfileDao(userProfileDaoConfig, this);

        registerDao(UserProfile.class, userProfileDao);
    }
    
    public void clear() {
        userProfileDaoConfig.clearIdentityScope();
    }

    public UserProfileDao getUserProfileDao() {
        return userProfileDao;
    }

}
