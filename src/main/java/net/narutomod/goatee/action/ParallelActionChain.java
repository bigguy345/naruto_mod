package net.narutomod.goatee.action;


import net.narutomod.goatee.action.api.IAction;
import net.narutomod.goatee.action.api.IActionChain;

import java.util.function.Consumer;

public class ParallelActionChain implements IActionChain {
    private final ActionManager scriptedActionManager;
    private int offset = 0, idx = 0;

    public ParallelActionChain(ActionManager scriptedActionManager) {
        this.scriptedActionManager = scriptedActionManager;
    }

    /**
     * schedule the next task ‘delay’ ticks after the previous one, fully in parallel
     */
    @Override
    public IActionChain after(int delay, String name, Consumer<IAction> task) {
        offset += delay;
        Consumer<IAction> wrapper = act -> {
            task.accept(act);
            act.markDone();
        };
        IAction a = scriptedActionManager.create(name,
            offset,
            wrapper);
        idx++;
        a.setUpdateEveryXTick(1);
        scriptedActionManager.scheduleParallelAction(a);
        return this;
    }

    @Override
    public IActionChain after(int delay, Consumer<IAction> task) {
        return after(delay, "parallel#" + idx, task);
    }
}
