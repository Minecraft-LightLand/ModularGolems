package dev.xkmc.modulargolems.content.client.pose;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.sounds.SoundEvents;
import static dev.xkmc.modulargolems.content.client.pose.PoseStateMachine.State.*;
public class PoseStateMachine {
    private final MetalGolemEntity mg;
    public PoseStateMachine(MetalGolemEntity mg){
        this.mg=mg;
    }
    public enum State {RaiseArms, LayArmsDown, StartAttacking}
    private PoseStateMachine.State state = LayArmsDown;
    public void tick() {
        int atkTick = mg.getAttackAnimationTick();
        if(!mg.level().isClientSide){return;}
        if (mg.isAggressive()) {signalWarning();}
        else{signalEndWarning();}
        if (atkTick <= 0) {signalEndAttacking();}
    }
    public void signalWarning() {
        if (state == LayArmsDown) {
            state = RaiseArms;
            mg.warningAnimationState.start(mg.tickCount);
        }
    }
    public void signalEndWarning() {
        if (state == RaiseArms) {
            state = LayArmsDown;
            mg.warningAnimationState.stop();
        }
    }
    public void signalAttacking() {
        if (state == RaiseArms || state == LayArmsDown) {
            state = StartAttacking;
            mg.warningAnimationState.stop();
            mg.attackAnimationState.start(mg.tickCount);
            mg.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        }
    }
    public void signalEndAttacking() {
        if (state == StartAttacking) {
            state = LayArmsDown;
            mg.attackAnimationState.stop();
        }
    }

}