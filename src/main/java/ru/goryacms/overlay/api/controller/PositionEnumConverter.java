package ru.goryacms.overlay.api.controller;

import java.beans.PropertyEditorSupport;

public class PositionEnumConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String s = text.toUpperCase();
        Position position = Position.valueOf(s);
        setValue(position);
    }
}
