package cn.jiming.jdlearning.action;

import java.util.Arrays;
import java.util.List;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Pipeline;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

public class HumanActionDetectionTranslator implements Translator<Image, NDArray> {

    private final List<String> synset;

    public HumanActionDetectionTranslator() {
        // species name
        synset = Arrays.asList("setosa", "versicolor", "virginica");
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
    	NDArray data = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
    	data = data.toType(DataType.FLOAT32, false);
    	System.out.println(data);
    	
    	 Pipeline pipeline = new Pipeline();
         pipeline
                 .add(new Resize(250))
                 .add(new ToTensor())
                 .add(new Normalize(
                                 new float[] {128f, 128f, 128f},
                                 new float[] {1/256f}));
    	
    	
    	
        return pipeline.transform(new NDList(data));
    }

    @Override
    public NDArray processOutput(TranslatorContext ctx, NDList list) {
    	NDArray result = list.get(1);
    	System.out.println(result);
    	
        return null;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}
