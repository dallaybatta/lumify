package com.altamiracorp.lumify.core.user;

import com.altamiracorp.bigtable.model.user.ModelUserContext;
import com.altamiracorp.lumify.core.model.user.UserType;
import com.altamiracorp.securegraph.Authorizations;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Authorizations authorizations;
    private String username;
    private String userId;
    private String currentWorkspace;
    private ModelUserContext modelUserContext;
    private UserType userType;

    public User(String userId, String username, String currentWorkspace, ModelUserContext modelUserContext, UserType userType, Authorizations authorizations) {
        this.userId = userId;
        this.username = username;
        this.currentWorkspace = currentWorkspace;
        this.modelUserContext = modelUserContext;
        this.userType = userType;
        this.authorizations = authorizations;
    }

    public String getUserId() {
        return userId;
    }

    public ModelUserContext getModelUserContext() {
        return modelUserContext;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentWorkspace() {
        return currentWorkspace;
    }

    public void setCurrentWorkspace(String currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
    }

    public UserType getUserType() {
        return userType;
    }

    public Authorizations getAuthorizations() {
        return authorizations;
    }
}
