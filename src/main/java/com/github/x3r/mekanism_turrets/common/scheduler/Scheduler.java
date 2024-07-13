package com.github.x3r.mekanism_turrets.common.scheduler;


import com.github.x3r.mekanism_turrets.MekanismTurrets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = MekanismTurrets.MOD_ID, value = Dist.DEDICATED_SERVER)
public final class Scheduler {

    static final ConcurrentMap<Integer, List<Runnable>> SERVER_SCHEDULE = new ConcurrentHashMap<>();

    private Scheduler() {

    }

    public static void schedule(Runnable task, int delay) {
        SERVER_SCHEDULE.compute(ServerLifecycleHooks.getCurrentServer().getTickCount() + delay,
                (integer, runnables) -> {
                    if(runnables == null) {
                        runnables = new ObjectArrayList<>();
                    }
                    runnables.add(task);
                    return runnables;
                });
    }
    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        int ticks = ServerLifecycleHooks.getCurrentServer().getTickCount();
        List<Runnable> tasks = SERVER_SCHEDULE.get(ticks);
        if(tasks != null) {
            tasks.forEach(Runnable::run);
            SERVER_SCHEDULE.remove(ticks);
        }
    }
}
