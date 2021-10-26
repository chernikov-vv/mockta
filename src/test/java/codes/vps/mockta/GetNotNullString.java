package codes.vps.mockta;

import lombok.Getter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class GetNotNullString extends BaseMatcher<Object> implements Recorder<String> {

    @Getter
    private String recorded;

    @Override
    public boolean matches(Object o) {
        boolean ok = o instanceof String;
        if (!ok) { return false; }
        recorded = (String)o;
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Value(recorded) is not null");
    }
}
