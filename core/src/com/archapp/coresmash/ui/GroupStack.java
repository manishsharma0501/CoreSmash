package com.archapp.coresmash.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.Stack;


final public class GroupStack implements UIComponent {
    private Container<Group> root;
    private Stack<Group> history;

    public GroupStack() {
        root = new Container<>();
        root.fill();
        root.setTouchable(Touchable.enabled);
        root.addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                event.handle();
                return true;
            }
        });

        history = new Stack<>();
    }

    public void push(Group group) {
        history.push(group);
        root.setActor(group);
    }

    public void pop() {
        history.pop();
        root.setActor(history.peek());
    }

    public int size() {
        return history.size();
    }

    public void setRoot(Group group) {
        history.clear();
        push(group);
    }

    public void restoreRoot() {
        Group origin = history.firstElement();
        setRoot(origin);
    }

    public void setBackground(Drawable background) {
        root.setBackground(background);
    }

    @Override
    public Group getRoot() {
        return root;
    }
}
