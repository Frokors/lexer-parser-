package com.company;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexTokenizer implements Enumeration<Token> {

    private final String content;

    private final ITokenType[] tokenTypes;

    private final Matcher matcher;

    private int currentPosition = 0;

    public RegexTokenizer(String content, ITokenType[] tokenTypes) {
        this.content = content;
        this.tokenTypes = tokenTypes;

        List<String> regexList = new ArrayList<>();
        for (int i = 0; i < tokenTypes.length; i++) {
            ITokenType tokenType = tokenTypes[i];
            regexList.add("(?<g" + i + ">" + tokenType.getRegex() + ")");
        }
        String regex = String.join("|", regexList);

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        matcher = pattern.matcher(content);
        matcher.find();
    }

    @Override
    public boolean hasMoreElements() {
        return currentPosition < content.length();
    }

    @Override
    public Token nextElement() {
        boolean found = currentPosition <= matcher.start() || matcher.find();

        int start = found ? matcher.start() : content.length();
        int end = found ? matcher.end() : content.length();

        if(found && currentPosition == start) {
            currentPosition = end;
            // Лексема найдена- определяем тип
            for (int i = 0; i < tokenTypes.length; i++) {
                String si = "g" + i;
                if (matcher.start(si) == start && matcher.end(si) == end) {
                    return createToken(content, tokenTypes[i], start, end);
                }
            }
        }
        throw new IllegalStateException("Не удается определить лексему в позиции " + currentPosition);
    }

    protected Token createToken(String content, ITokenType tokenType, int start, int end) {
        return new Token(content.substring(start, end), tokenType, start);
    }

    public enum TokenType implements ITokenType {
        KEYWORD("\\b(?:var)|(?:VAR)\\b"),
        ID("[A-Za-z][A-Za-z0-9]*"),
        REAL_NUMBER("[0-9]+\\.[0-9]*"),
        NUMBER("[0-9]+"),
        STRING("'[^']*'"),
        SPACE("\\s+"),
        COMMENT("\\-\\-[^\\n\\r]*"),
        OPERATION("[+\\-\\*/.=\\(\\)]");

        private final String regex;

        TokenType(String regex) {
            this.regex = regex;
        }

        @Override
        public String getRegex() {
            return regex;
        }
    }

    public static void main(String[] args) {
        String s = """
                var a
                var b
                var c = a + b """;
        RegexTokenizer tokenizer = new RegexTokenizer(s, TokenType.values());
        while(tokenizer.hasMoreElements()) {
            Token token = tokenizer.nextElement();
            System.out.println(token.getText() + " : " + token.getType());
        }
    }
}
