package mrgeoves.weatherascended.server.world;

import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public class AscendedWeatherWorld extends ServerWorld {
    private boolean inBlockTick;

    public AscendedWeatherWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<SpecialSpawner> spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState, ServerWorldProperties worldProperties) {
        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
        this.worldProperties = worldProperties;
    }
    private final ServerWorldProperties worldProperties;
    public static final IntProvider CLEAR_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 180000);
    public static final IntProvider RAIN_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 24000);
    private static final IntProvider CLEAR_THUNDER_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 180000);
    public static final IntProvider THUNDER_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(3600, 15600);
    public static final IntProvider CLEAR_MONSOON_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 180000);
    public static final IntProvider MONSOON_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(6000, 18000);

    public interface WorldProperties {
        boolean isMonsooning();
    }
    public void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering, boolean monsooning) {
        this.worldProperties.setClearWeatherTime(clearDuration);
        this.worldProperties.setRainTime(rainDuration);
        this.worldProperties.setThunderTime(rainDuration);
        this.worldProperties.setRaining(raining);
        this.worldProperties.setThundering(thundering);
        this.worldProperties.setMonsooning(monsooning);
    }
    public void tick(BooleanSupplier shouldKeepTicking) {
        Profiler profiler = this.getProfiler();
        this.inBlockTick = true;
        TickManager tickManager = this.getTickManager();
        boolean bl = tickManager.shouldTick();
        if (bl) {
            profiler.push("world border");
            this.getWorldBorder().tick();
            profiler.swap("weather");
            this.tickweatherAscended();
        }
    }
    private void tickweatherAscended() {
        boolean bl = this.isRaining();
        if (this.getDimension().hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                int i = this.worldProperties.getClearWeatherTime();
                int j = this.worldProperties.getThunderTime();
                int k = this.worldProperties.getRainTime();
                int l = this.worldProperties.is
                boolean bl2 = this.properties.isThundering();
                boolean bl3 = this.properties.isRaining();
                boolean bl4 = this.properties.isMonsooning();
                if (i > 0) {
                    --i;
                    j = bl2 ? 0 : 1;
                    k = bl3 ? 0 : 1;
                    l = bl4
                    bl2 = false;
                    bl3 = false;
                } else {
                    if (j > 0) {
                        --j;
                        if (j == 0) {
                            bl2 = !bl2;
                        }
                    } else if (bl2) {
                        j = THUNDER_WEATHER_DURATION_PROVIDER.get(this.random);
                    } else {
                        j = CLEAR_THUNDER_WEATHER_DURATION_PROVIDER.get(this.random);
                    }

                    if (k > 0) {
                        --k;
                        if (k == 0) {
                            bl3 = !bl3;
                        }
                    } else if (bl3) {
                        k = RAIN_WEATHER_DURATION_PROVIDER.get(this.random);
                    } else {
                        k = CLEAR_WEATHER_DURATION_PROVIDER.get(this.random);
                    }
                }

                this.worldProperties.setThunderTime(j);
                this.worldProperties.setRainTime(k);
                this.worldProperties.setClearWeatherTime(i);
                this.worldProperties.setThundering(bl2);
                this.worldProperties.setRaining(bl3);
            }

            this.thunderGradientPrev = this.thunderGradient;
            if (this.properties.isThundering()) {
                this.thunderGradient += 0.01F;
            } else {
                this.thunderGradient -= 0.01F;
            }

            this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
            this.rainGradientPrev = this.rainGradient;
            if (this.properties.isRaining()) {
                this.rainGradient += 0.01F;
            } else {
                this.rainGradient -= 0.01F;
            }

            this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
        }

        if (this.rainGradientPrev != this.rainGradient) {
            this.getServer().getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, this.rainGradient), this.getRegistryKey());
        }

        if (this.thunderGradientPrev != this.thunderGradient) {
            this.getServer().getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, this.thunderGradient), this.getRegistryKey());
        }

        if (bl != this.isRaining()) {
            if (bl) {
                this.getServer().getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STOPPED, 0.0F));
            } else {
                this.getServer().getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0F));
            }

            this.getServer().getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, this.rainGradient));
            this.getServer().getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, this.thunderGradient));
        }

    }
}
