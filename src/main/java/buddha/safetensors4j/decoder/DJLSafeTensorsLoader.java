package buddha.safetensors4j.decoder;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Parameter;
import ai.djl.nn.ParameterList;
import buddha.safetensors4j.pojo.SafeTensors;
import lombok.Cleanup;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

public class DJLSafeTensorsLoader implements ISafeTensorsLoader<ai.djl.Model> {
    @Override
    public void load(SafeTensors tensorsDef, ai.djl.Model model) {
        final ParameterList parameterList = model.getBlock().getParameters();
        try (final FileInputStream fis = new FileInputStream(tensorsDef.getFilename())) {
            fis.readNBytes((int)(8 + tensorsDef.getSizeOfHeader())); // skip header
            final List<SafeTensors.HeaderElement> header = tensorsDef.getHeader();
            @Cleanup final NDManager nm = NDManager.newBaseManager();
            for (final SafeTensors.HeaderElement element : header) {
                final long size = element.getOffsets().get(1) - element.getOffsets().get(0);
                final byte[] rawParams = fis.readNBytes((int)size);
                DataType.fromSafetensors(element.getDataType().toString());
                final NDArray paramTensor = nm.create(
                        ByteBuffer.wrap(rawParams),
                        new Shape(element.getShape()),
                        DataType.fromSafetensors(element.getDataType().toString())
                );
                final Parameter param = Parameter.builder().optArray(paramTensor).build();
                parameterList.add(element.getName(), param);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
