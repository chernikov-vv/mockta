package codes.vps.mockta.util;

import java.util.NoSuchElementException;

public class ForwardString extends StringWinder {

    private int index;
    private final int limit;
    private boolean expired;

    public ForwardString(CharSequence src) {

        super(src);
        int limit = src.length();
        index = 0;
        expired = limit <= 0;
        this.limit = limit;

    }

    @Override
    public boolean hasNext() {
        return !expired;
    }

    @Override
    public char next() {

        char c = peek();

        if (++index == limit) { expired = true; }

        return c;

    }

    @Override
    public char peek() {
        if (expired) {
            throw new NoSuchElementException("ran out");
        }
        return array[index];
    }

    @Override
    public String remainder() {
        return String.valueOf(array, index, limit - index);
    }

    public String toString() {
        return String.valueOf(array, 0, index) + '‸' + remainder();
    }

    @Override
    public int getLastIndex() {
        return index - 1;
    }

}
