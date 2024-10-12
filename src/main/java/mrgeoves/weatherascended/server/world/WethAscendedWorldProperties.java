package mrgeoves.weatherascended.server.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldProperties;

public class WethAscendedWorldProperties implements WorldProperties {

    @Override
    public BlockPos getSpawnPos() {
        return null;
    }

    @Override
    public float getSpawnAngle() {
        return 0;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public long getTimeOfDay() {
        return 0;
    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public void setRaining(boolean raining) {

    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public GameRules getGameRules() {
        return null;
    }

    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @Override
    public boolean isDifficultyLocked() {
        return false;
    }
    public boolean isMonsooning() {
        return false;
    }
}
