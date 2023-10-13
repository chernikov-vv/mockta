package codes.vps.mockta.util;

import java.util.List;

public record Page<V, K>(long total, List<V> data, K next) {

}
