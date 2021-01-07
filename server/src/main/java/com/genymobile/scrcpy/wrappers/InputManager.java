package com.genymobile.scrcpy.wrappers;


import android.os.IInterface;
import android.view.InputEvent;

import com.genymobile.scrcpy.Ln;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class InputManager {

    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;

    private final IInterface manager;
    private Method injectInputEventMethod;

    private static Method setDisplayIdMethod;

    public InputManager(IInterface manager) {
        this.manager = manager;
    }

    private Method getInjectInputEventMethod() throws NoSuchMethodException {
        if (injectInputEventMethod == null) {
            try {
                injectInputEventMethod = manager.getClass().getMethod("injectInputEventForDisplay", InputEvent.class, int.class, int.class);
            } catch (Exception e) {
                injectInputEventMethod = manager.getClass().getMethod("injectInputEvent", InputEvent.class, int.class);
            }
        }
        return injectInputEventMethod;
    }

    public boolean injectInputEvent(InputEvent inputEvent, int displayId, int mode) {
        try {
            Method method = getInjectInputEventMethod();
            boolean ret = false;
            try {
                //noinspection ConstantConditions
                ret = (boolean) method.invoke(manager, inputEvent, displayId, mode);
            } catch (Exception e) {
                method.invoke(manager, inputEvent, mode);
            }
            return ret;
        } catch (Exception e) {
            Ln.e("Could not invoke method", e);
            return false;
        }
    }

    private static Method getSetDisplayIdMethod() throws NoSuchMethodException {
        if (setDisplayIdMethod == null) {
            setDisplayIdMethod = InputEvent.class.getMethod("setDisplayId", int.class);
        }
        return setDisplayIdMethod;
    }

    public static boolean setDisplayId(InputEvent inputEvent, int displayId) {
        try {
            Method method = getSetDisplayIdMethod();
            method.invoke(inputEvent, displayId);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Ln.e("Cannot associate a display id to the input event", e);
            return false;
        }
    }
}
