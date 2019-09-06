package com.froobworld.saml.config;

public final class ConfigKeys {
    public static final String VERSION = "version";

    // features
    public static final String CNF_USE_ADVANCED_CONFIG = "use-advanced-config";
    public static final String CNF_ENABLE_PASSIVE_FREEZE_TASK = "enable-passive-freeze-task";
    public static final String CNF_TICKS_PER_PASSIVE_FREEZE_TASK = "ticks-per-passive-freeze-task";
    public static final String CNF_ENABLE_TPS_FREEZE_TASK = "enable-tps-freeze-task";
    public static final String CNF_TICKS_PER_TPS_FREEZE_TASK = "ticks-per-tps-freeze-task";

    // frozen_chunk_cache_settings
    public static final String CNF_KEEP_FROZEN_CHUNK_CACHE = "keep-frozen-chunk-cache";
    public static final String CNF_UNFREEZE_CACHED_CHUNKS_ON_STARTUP = "unfreeze-cached-chunks-on-startup";
    public static final String CNF_TICKS_PER_CACHED_CHUNK_UNFREEZE = "ticks-per-cached-chunk-unfreeze";
    public static final String CNF_USE_PAPER_GET_CHUNK_ASYNC = "use-paper-get-chunk-async";

    // passive_freeze_parameters
    public static final String CNF_PASSIVE_FREEZE_GROUPS = "passive-freeze-groups";
    public static final String CNF_PASSIVE_FREEZE_EXCLUDE_GROUPS = "passive-freeze-exclude-groups";
    public static final String CNF_PASSIVE_NERF_GROUPS = "passive-nerf-groups";
    public static final String CNF_PASSIVE_NERF_EXCLUDE_GROUPS = "passive-nerf-exclude-groups";
    public static final String CNF_PASSIVE_DO_CLEANUP = "passive-do-cleanup";
    public static final String CNF_PASSIVE_FREEZE_BROADCAST_TO_CONSOLE = "passive-freeze-broadcast-to-console";
    public static final String CNF_PASSIVE_FREEZE_BROADCAST_TO_OPS = "passive-freeze-broadcast-to-ops";

    // tps_freeze_parameters
    public static final String CNF_TPS_FREEZE_GROUPS = "tps-freeze-groups";
    public static final String CNF_TPS_FREEZE_EXCLUDE_GROUPS = "tps-freeze-exclude-groups";
    public static final String CNF_TPS_NERF_GROUPS = "tps-nerf-groups";
    public static final String CNF_TPS_NERF_EXCLUDE_GROUPS = "tps-nerf-exclude-groups";
    public static final String CNF_TPS_FREEZING_THRESHOLD = "tps-freezing-threshold";
    public static final String CNF_TPS_UNFREEZING_THRESHOLD = "tps-unfreezing-threshold";
    public static final String CNF_TPS_UNFREEZE_CONFIDENCE_RANGE = "tps-unfreeze-confidence-range";
    public static final String CNF_TPS_UNFREEZE_LIMIT = "tps-unfreeze-limit";
    public static final String CNF_TPS_MINIMUM_FREEZE_TIME = "tps-minimum-freeze-time";
    public static final String CNF_TPS_DO_CLEANUP = "tps-do-cleanup";
    public static final String CNF_TPS_FREEZE_BROADCAST_TO_CONSOLE = "tps-freeze-broadcast-to-console";
    public static final String CNF_TPS_FREEZE_BROADCAST_TO_OPS = "tps-freeze-broadcast-to-ops";

    // tps_calculator_settings
    public static final String CNF_TPS_SAMPLE_SIZE = "tps-sample-size";
    public static final String CNF_TPS_WEIGHTING_FACTOR = "tps-weighting-factor";
    public static final String CNF_TPS_SMOOTHING_SAMPLE_SIZE = "tps-smoothing-sample-size";
    public static final String CNF_USE_NMS_TPS = "use-nms-tps";
    public static final String CNF_TPS_DEVIATION_SAMPLE_RATE = "tps-deviation-sample-rate";
    public static final String CNF_TPS_DEVIATION_SAMPLE_SIZE = "tps-deviation-sample-size";

    // generic_freeze_parameters
    public static final String CNF_USE_ASYNC_GROUPING = "use-async-grouping";
    public static final String CNF_MAXIMUM_OPERATION_TIME = "maximum-operation-time";

    // default_group_settings
    public static final String CNF_GROUP_MINIMUM_SIZE = "group-minimum-size";
    public static final String CNF_GROUP_MAXIMUM_RADIUS = "group-maximum-radius";
    public static final String CNF_GROUP_REQUIRE_SAME_TYPE = "group-require-same-type";
    public static final String CNF_GROUP_USE_SMART_SCALING = "group-use-smart-scaling";
    public static final String CNF_GROUP_MINIMUM_SCALED_SIZE = "group-minimum-scaled-size";
    public static final String CNF_GROUP_MAXIMUM_SCALED_RADIUS = "group-maximum-scaled-radius";
    public static final String CNF_GROUP_MINIMUM_SCALE_TPS_RATIO = "group-minimum-scale-tps-ratio";

    // compatibility_settings
    public static final String CNF_IGNORE_METADATA = "ignore-metadata";
    public static final String CNF_IGNORE_WORLD = "ignore-world";
    public static final String CNF_ONLY_UNFREEZE_TAGGED = "only-unfreeze-tagged";
    public static final String CNF_UNFREEZE_ON_UNLOAD = "unfreeze-on-unload";
    public static final String CNF_UNFREEZE_ON_SHUTDOWN = "unfreeze-on-shutdown";

    // gameplay_settings
    public static final String CNF_PREVENT_TARGETING_FROZEN = "prevent-targeting-frozen";
    public static final String CNF_PREVENT_DAMAGING_FROZEN = "prevent-damaging-frozen";
    public static final String CNF_PREVENT_PLAYER_DAMAGING_FROZEN = "prevent-player-damaging-frozen";
    public static final String CNF_UNFREEZE_ON_INTERACT = "unfreeze-on-interact";
    public static final String CNF_UNFREEZE_ON_INTERACT_TPS_THRESHOLD = "unfreeze-on-interact-tps-threshold";
    public static final String CNF_UNFREEZE_ON_DAMAGE = "unfreeze-on-damage";
    public static final String CNF_UNFREEZE_ON_DAMAGE_TPS_THRESHOLD = "unfreeze-on-damage-tps-threshold";
    public static final String CNF_IGNORE_TAMED = "ignore-tamed";
    public static final String CNF_IGNORE_TAMED_TPS_THRESHOLD = "ignore-tamed-tps-threshold";
    public static final String CNF_IGNORE_NAMED = "ignore-named";
    public static final String CNF_IGNORE_NAMED_TPS_THRESHOLD = "ignore-named-tps-threshold";
    public static final String CNF_IGNORE_LEASHED = "ignore-leashed";
    public static final String CNF_IGNORE_LEASHED_TPS_THRESHOLD = "ignore-leashed-tps-threshold";
    public static final String CNF_IGNORE_LOVE_MODE = "ignore-love-mode";
    public static final String CNF_IGNORE_LOVE_MODE_TPS_THRESHOLD = "ignore-love-mode-tps-threshold";
    public static final String CNF_IGNORE_PLAYER_PROXIMITY = "ignore-player-proximity";
    public static final String CNF_IGNORE_PLAYER_PROXIMITY_TPS_THRESHOLD = "ignore-player-proximity-tps-threshold";
    public static final String CNF_IGNORE_YOUNGER_THAN_TICKS = "ignore-younger-than-ticks";
    public static final String CNF_IGNORE_YOUNGER_THAN_TICKS_TPS_THRESHOLD = "ignore-younger-than-ticks-tps-threshold";
    public static final String CNF_IGNORE_TARGET_PLAYER = "ignore-target-player";
    public static final String CNF_IGNORE_TARGET_PLAYER_TPS_THRESHOLD = "ignore-target-player-tps-threshold";
    public static final String CNF_NEVER_FREEZE = "never-freeze";
    public static final String CNF_NEVER_FREEZE_TPS_THRESHOLD = "never-freeze-tps-threshold";


    public static final String MSG_TPS_FREEZE_START_CONSOLE = "tps-freeze-start-console";
    public static final String MSG_TPS_FREEZE_START_OPS = "tps-freeze-start-ops";
    public static final String MSG_TPS_FREEZE_COMPLETE_CONSOLE = "tps-freeze-complete-console";
    public static final String MSG_TPS_FREEZE_COMPLETE_OPS = "tps-freeze-complete-ops";

    public static final String MSG_PASSIVE_FREEZE_START_CONSOLE = "passive-freeze-start-console";
    public static final String MSG_PASSIVE_FREEZE_START_OPS = "passive-freeze-start-ops";
    public static final String MSG_PASSIVE_FREEZE_COMPLETE_CONSOLE = "passive-freeze-complete-console";
    public static final String MSG_PASSIVE_FREEZE_COMPLETE_OPS = "passive-freeze-complete-ops";

    public static final String ADV_PREVENT_TARGETING_FROZEN = "prevent-targeting-frozen";
    public static final String ADV_PREVENT_DAMAGING_FROZEN = "prevent-damaging-frozen";
    public static final String ADV_PREVENT_PLAYER_DAMAGING_FROZEN = "prevent-player-damaging-frozen";
    public static final String ADV_UNFREEZE_ON_INTERACT = "unfreeze-on-interact";
    public static final String ADV_UNFREEZE_ON_INTERACT_TPS_THRESHOLD = "unfreeze-on-interact-tps-threshold";
    public static final String ADV_UNFREEZE_ON_DAMAGE = "unfreeze-on-damage";
    public static final String ADV_UNFREEZE_ON_DAMAGE_TPS_THRESHOLD = "unfreeze-on-damage-tps-threshold";
    public static final String ADV_IGNORE_TAMED = "ignore-tamed";
    public static final String ADV_IGNORE_TAMED_TPS_THRESHOLD = "ignore-tamed-tps-threshold";
    public static final String ADV_IGNORE_NAMED = "ignore-named";
    public static final String ADV_IGNORE_NAMED_TPS_THRESHOLD = "ignore-named-tps-threshold";
    public static final String ADV_IGNORE_LEASHED = "ignore-leashed";
    public static final String ADV_IGNORE_LEASHED_TPS_THRESHOLD = "ignore-leashed-tps-threshold";
    public static final String ADV_IGNORE_LOVE_MODE = "ignore-love-mode";
    public static final String ADV_IGNORE_LOVE_MODE_TPS_THRESHOLD = "ignore-love-mode-tps-threshold";
    public static final String ADV_IGNORE_PLAYER_PROXIMITY = "ignore-player-proximity";
    public static final String ADV_IGNORE_PLAYER_PROXIMITY_TPS_THRESHOLD = "ignore-player-proximity-tps-threshold";
    public static final String ADV_IGNORE_YOUNGER_THAN_TICKS = "ignore-younger-than-ticks";
    public static final String ADV_IGNORE_YOUNGER_THAN_TICKS_TPS_THRESHOLD = "ignore-younger-than-ticks-tps-threshold";
    public static final String ADV_IGNORE_TARGET_PLAYER = "ignore-target-player";
    public static final String ADV_IGNORE_TARGET_PLAYER_TPS_THRESHOLD = "ignore-target-player-tps-threshold";
    public static final String ADV_NEVER_FREEZE_TPS_THRESHOLD = "never-freeze-tps-threshold";

    public static final String NRF_UNSPECIFIED = "unspecified";
}
