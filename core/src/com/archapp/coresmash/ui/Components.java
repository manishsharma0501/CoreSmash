package com.archapp.coresmash.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class Components {
    private static Toast toast;

    // Disable constructor
    private Components() {
    }

    public static void initialize(Skin skin) {
            Toast.ToastStyle ws = new Toast.ToastStyle();
            ws.background = skin.getDrawable("toast1");
            ws.font = skin.getFont("h5");

            toast = new Toast(ws);
    }

    public static void clearToasts() {
        toast.hide(null);
    }

    public static void showToast(String text, Stage stage) {
        showToast(text, stage, 2.5f);
    }

    public static void showToast(String text, Stage stage, float duration) {
        toast.setText(text)
                .show(stage, Actions.sequence(
                        Actions.alpha(0),
                        Actions.fadeIn(.4f),
                        Actions.delay(duration),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                toast.hide(Actions.fadeOut(.4f));
                            }
                        })));
    }
}
