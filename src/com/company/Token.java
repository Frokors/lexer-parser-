package com.company;

public class Token {

    private final String text;

    private final ITokenType type;

    private final int start;

    public Token(String text, ITokenType type, int start) {
        this.text = text;
        this.type = type;
        this.start = start;
    }

    public String getText() {
        return text;
    }

    public ITokenType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }
}