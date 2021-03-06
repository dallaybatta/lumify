package com.altamiracorp.lumify.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.utils.Utils;
import com.altamiracorp.lumify.core.cmdline.CommandLineBase;
import com.altamiracorp.lumify.core.model.workQueue.WorkQueueRepository;
import com.altamiracorp.lumify.model.LumifyKafkaSpout;
import com.google.inject.Inject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public abstract class StormRunnerBase extends CommandLineBase {
    private static final String CMD_OPT_LOCAL = "local";
    private static final String CMD_OPT_TASKS_PER_BOLT = "tasksperbolt";
    private static final String CMD_OPT_NUM_WORKERS = "workers";
    private static final String CMD_OPT_PARALLELISM_HINT = "parallelismhint";
    private static final String CMD_OPT_REPROCESS_ALL_QUEUES = "reprocessallqueues";
    private boolean local;
    private boolean reprocessAllQueues;
    private WorkQueueRepository workQueueRepository;

    public StormRunnerBase() {
        initFramework = true;
    }

    @Override
    protected Options getOptions() {
        Options opts = super.getOptions();

        opts.addOption(
                OptionBuilder
                        .withLongOpt(CMD_OPT_LOCAL)
                        .withDescription("Run local")
                        .create("l")
        );

        opts.addOption(
                OptionBuilder
                        .withLongOpt(CMD_OPT_TASKS_PER_BOLT)
                        .withDescription("Max number of storm tasks")
                        .hasArg()
                        .withArgName("count")
                        .create("tpb")
        );

        opts.addOption(
                OptionBuilder
                        .withLongOpt(CMD_OPT_NUM_WORKERS)
                        .withDescription("Number of workers")
                        .hasArg()
                        .withArgName("count")
                        .create("w")
        );

        opts.addOption(
                OptionBuilder
                        .withLongOpt(CMD_OPT_PARALLELISM_HINT)
                        .withDescription("Parallelism hint")
                        .hasArg()
                        .withArgName("count")
                        .create("ph")
        );

        opts.addOption(
                OptionBuilder
                        .withLongOpt(CMD_OPT_REPROCESS_ALL_QUEUES)
                        .withDescription("Tells kafka spouts to start from earliest offset.")
                        .create("raq")
        );

        return opts;
    }

    @Override
    protected int run(CommandLine cmd) throws Exception {
        local = cmd.hasOption(CMD_OPT_LOCAL);
        reprocessAllQueues = cmd.hasOption(CMD_OPT_REPROCESS_ALL_QUEUES);

        int parallelismHint = 1;
        if (cmd.hasOption(CMD_OPT_PARALLELISM_HINT)) {
            parallelismHint = Integer.parseInt(cmd.getOptionValue(CMD_OPT_PARALLELISM_HINT));
        }

        Config conf = createConfig(cmd);

        beforeCreateTopology(cmd, conf);

        LOGGER.info("Creating topology: " + getTopologyName());
        StormTopology topology = createTopology(parallelismHint);
        LOGGER.info("Created topology layout: " + topology);
        LOGGER.info(String.format("Submitting topology '%s'", getTopologyName()));

        if (local) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(getTopologyName(), conf, topology);

            while (!willExit()) {
                Utils.sleep(100);
            }

            cluster.killTopology(getTopologyName());
            cluster.shutdown();
        } else {
            LOGGER.info("submitting topology: " + getTopologyName());
            StormSubmitter.submitTopology(getTopologyName(), conf, topology);
            LOGGER.info("topology submitted: " + getTopologyName());
        }

        return 0;
    }

    protected void beforeCreateTopology(CommandLine cmd, Config conf) throws Exception {

    }

    protected Config createConfig(CommandLine cmd) {
        Config conf = new Config();
        conf.put("topology.kryo.factory", "com.altamiracorp.lumify.storm.DefaultKryoFactory");
        for (String key : getConfiguration().getKeys()) {
            conf.put(key, getConfiguration().get(key));
        }
        conf.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, 10000);
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 100);
        if (cmd.hasOption(CMD_OPT_TASKS_PER_BOLT)) {
            conf.put(Config.TOPOLOGY_TASKS, Integer.parseInt(cmd.getOptionValue(CMD_OPT_TASKS_PER_BOLT)));
        }
        if (cmd.hasOption(CMD_OPT_NUM_WORKERS)) {
            conf.setNumWorkers(Integer.parseInt(cmd.getOptionValue(CMD_OPT_NUM_WORKERS)));
        }
        conf.setDebug(false);
        return conf;
    }

    protected abstract String getTopologyName();

    protected abstract StormTopology createTopology(int parallelismHint);

    protected boolean isLocal() {
        return local;
    }

    public Long getQueueStartOffsetTime() {
        if (this.reprocessAllQueues) {
            return LumifyKafkaSpout.KAFKA_START_OFFSET_TIME_EARLIEST;
        }
        return null;
    }

    protected IRichSpout createWorkQueueRepositorySpout(String queueName) {
        return (IRichSpout) this.workQueueRepository.createSpout(getConfiguration(), queueName, getQueueStartOffsetTime());
    }

    @Inject
    public void setWorkQueueRepository(WorkQueueRepository workQueueRepository) {
        this.workQueueRepository = workQueueRepository;
    }
}
