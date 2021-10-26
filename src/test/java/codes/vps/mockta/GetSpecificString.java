package codes.vps.mockta;

import lombok.Getter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class GetSpecificString extends BaseMatcher<Object> implements Recorder<String> {

    @Getter
    private String recorded;

    private final String expected;

    public GetSpecificString(String expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object o) {
        boolean ok = expected.equals(o);
        if (!ok) { return false; }
        recorded = (String)o;
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Value(recorded) expected to be "+expected);
    }

}
