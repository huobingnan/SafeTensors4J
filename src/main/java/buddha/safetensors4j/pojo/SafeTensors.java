package buddha.safetensors4j.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * SafeTensors Plain Old Java Object
 */
@Data
public class SafeTensors {

    public static enum DataType {
        F64, F32, F16, BF16,
        I64, I32, I16, I8, BOOL
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeaderElement {
        private String name;
        private DataType dataType;
        private List<Long> shape;
        private List<Long> offsets;
    }

    private long sizeOfHeader;
    private List<HeaderElement> header;
    private Map<String, String> metadata;
    private String filename;
}
