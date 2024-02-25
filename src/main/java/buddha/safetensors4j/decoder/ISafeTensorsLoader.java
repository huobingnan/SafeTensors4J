package buddha.safetensors4j.decoder;

import buddha.safetensors4j.pojo.SafeTensors;

/**
 * SafeTensors加载器
 */
public interface ISafeTensorsLoader<B> {

    public void load(SafeTensors tensorsDefinition, B blocks);
}
