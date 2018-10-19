package org.talend.components.azure.processor;

import org.apache.beam.sdk.testing.TestPipeline;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.talend.sdk.component.junit.SimpleComponentRule;

public class OutputTableProcessorBeamTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("org.talend.components.azure");

    @Rule
    public transient final TestPipeline pipeline = TestPipeline.create();

    @Test
    @Ignore("You need to complete this test with your own data and assertions")
    public void processor() {
        /*
         * // Processor configuration
         * // Setup your component configuration for the test here
         * final OutputTableProcessorConfiguration configuration = new OutputTableProcessorConfiguration();
         * 
         * // We create the component processor instance using the configuration filled above
         * final Processor processor = COMPONENT_FACTORY.createProcessor(OutputTableProcessor.class, configuration);
         * 
         * // The join input factory construct inputs test data for every input branch you have defined for this component
         * // Make sure to fil in some test data for the branches you want to test
         * // You can also remove the branches that you don't need from the factory below
         * final JoinInputFactory joinInputFactory = new JoinInputFactory().withInput("__default__",
         * asList(
         *//* TODO - list of your input data for this branch. Instances of OutputTableDefaultInput.class *//*
                                                                                                            * ));
                                                                                                            * 
                                                                                                            * // Convert it to a
                                                                                                            * beam "source"
                                                                                                            * final
                                                                                                            * PCollection<Record>
                                                                                                            * inputs =
                                                                                                            * pipeline.apply(Data.
                                                                                                            * of(processor.plugin(
                                                                                                            * ), joinInputFactory.
                                                                                                            * asInputRecords()));
                                                                                                            * 
                                                                                                            * // add our processor
                                                                                                            * right after to see
                                                                                                            * each data as
                                                                                                            * configured
                                                                                                            * previously
                                                                                                            * final
                                                                                                            * PCollection<Map<
                                                                                                            * String, Record>>
                                                                                                            * outputs =
                                                                                                            * inputs.apply(
                                                                                                            * TalendFn.asFn(
                                                                                                            * processor))
                                                                                                            * .apply(Data.map(
                                                                                                            * processor.plugin(),
                                                                                                            * Record.class));
                                                                                                            * 
                                                                                                            * PAssert.that(outputs
                                                                                                            * ).satisfies((
                                                                                                            * SerializableFunction
                                                                                                            * <Iterable<Map<
                                                                                                            * String, Record>>,
                                                                                                            * Void>) input -> {
                                                                                                            * final
                                                                                                            * List<Map<String,
                                                                                                            * Record>> result =
                                                                                                            * StreamSupport.stream
                                                                                                            * (input.spliterator()
                                                                                                            * , false).collect(
                                                                                                            * toList());
                                                                                                            * // TODO - test the
                                                                                                            * result here
                                                                                                            * 
                                                                                                            * return null;
                                                                                                            * });
                                                                                                            * // run the pipeline
                                                                                                            * and ensure the
                                                                                                            * execution was
                                                                                                            * successful
                                                                                                            * assertEquals(
                                                                                                            * PipelineResult.State
                                                                                                            * .DONE,
                                                                                                            * pipeline.run().
                                                                                                            * waitUntilFinish());
                                                                                                            */
    }
}