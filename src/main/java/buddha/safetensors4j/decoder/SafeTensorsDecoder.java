package buddha.safetensors4j.decoder;

import buddha.safetensors4j.pojo.SafeTensors;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * SafeTensors格式文件解析器
 */
@RequiredArgsConstructor
public class SafeTensorsDecoder {
    @NonNull private final String filename;

    private long getSizeOfHeader(byte[] sizeOfHeader) {
        long size = 0;
        for (int i = 7; i >= 0; i--) {
            size <<= 8;
            size = size | (sizeOfHeader[i] & 0xff);
        }
        return size;
    }
    public SafeTensors decode() {
        try (final FileInputStream fis = new FileInputStream(filename)) {
            final long headerSize = getSizeOfHeader(fis.readNBytes(8));
            final byte[] headerJsonBytes = fis.readNBytes((int)headerSize);
            final JSONObject headerJson = JSON.parseObject(headerJsonBytes);
            final SafeTensors safeTensors = new SafeTensors();
            safeTensors.setFilename(Paths.get(filename).toFile().getAbsolutePath());
            safeTensors.setSizeOfHeader(headerSize);
            final List<SafeTensors.HeaderElement> elements = new ArrayList<>((int)headerSize);
            for (final String name : headerJson.keySet()) {
                if ("__metadata__".equals(name)) {
                    final Map<String, String> metadata = headerJson.getJSONObject("__metadata__").toJavaObject(Map.class);
                    safeTensors.setMetadata(metadata);
                    continue;
                }
                final SafeTensors.HeaderElement element = new SafeTensors.HeaderElement();
                final JSONObject header = headerJson.getJSONObject(name);
                final String dataType = header.getString("dtype");
                final List<Long> shape = header.getList("shape", Long.class);
                final List<Long> offsets = header.getList("data_offsets", Long.class);
                element.setName(name);
                element.setDataType(SafeTensors.DataType.valueOf(dataType));
                element.setShape(shape);
                element.setOffsets(offsets);
                elements.add(element);
            }
            safeTensors.setHeader(elements);
            return safeTensors;
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
