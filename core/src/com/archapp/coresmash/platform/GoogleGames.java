package com.archapp.coresmash.platform;

import com.archapp.coresmash.PropertyChangeListener;

public interface GoogleGames {
    boolean isSignedIn();

    void signIn(OnRequestComplete callback);

    PlayerInfo getAccountInfo();

    void addListener(PropertyChangeListener listener);

    void removeListener(PropertyChangeListener listener);

    void signOut();

    interface OnRequestComplete {
        void onComplete(boolean result);
    }
}
