package buddha.safetensors4j.test.decoder;

import buddha.safetensors4j.decoder.SafeTensorsDecoder;
import buddha.safetensors4j.pojo.SafeTensors;
import org.junit.jupiter.api.Test;

public class SafeTensorDecoderTest {

    @Test
    public void testDecode() {
        final SafeTensorsDecoder decoder =
                new SafeTensorsDecoder("SafeTensorsModel/control_lora_rank128_v11e_sd15_ip2p_fp16.safetensors");
        final SafeTensors safeTensors = decoder.decode();
        System.out.println(safeTensors.getHeader().size());
        System.out.println(safeTensors.getMetadata());
        safeTensors.getHeader().stream().limit(10).forEach(it -> {
            System.out.printf("%s, %s, %s, %s\n", it.getName(), it.getShape(), it.getDataType(), it.getOffsets());
        });
    }
}
