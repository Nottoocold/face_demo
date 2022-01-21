package cn.jiming.jdlearning.face.feature;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Pipeline;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
/**
 * 返回ndarray特征数据
 * @author xiaoming
 */
public class FaceFeatureNDArrayTranslator implements Translator<Image, NDArray>{
	FaceFeatureNDArrayTranslator() {}

    /** {@inheritDoc} */
    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
        Pipeline pipeline = new Pipeline();
        pipeline
                .add(new Resize(160))
                .add(new ToTensor())
                .add(
                        new Normalize(
                                new float[] {127.5f / 255.0f, 127.5f / 255.0f, 127.5f / 255.0f},
                                new float[] {
                                    128.0f / 255.0f, 128.0f / 255.0f, 128.0f / 255.0f
                                }));

        return pipeline.transform(new NDList(array));
    }

    /** {@inheritDoc} */
    @Override
    public NDArray processOutput(TranslatorContext ctx, NDList list) {
    	NDArray feature = NDManager.newBaseManager().create(list.singletonOrThrow().toFloatArray());
        return feature;
    }

    /** {@inheritDoc} */
    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}

