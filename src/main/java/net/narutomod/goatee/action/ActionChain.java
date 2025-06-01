package net.narutomod.goatee.action;


import net.narutomod.goatee.action.api.IAction;
import net.narutomod.goatee.action.api.IActionChain;

import java.util.function.Consumer;



public class ActionChain implements IActionChain {
    private final ActionManager scriptedActionManager;
    private int offset = 0, index = 0;

    public ActionChain(ActionManager scriptedActionManager) {
        this.scriptedActionManager = scriptedActionManager;
    }

    /**
     * schedule the next task ‘delay’ ticks after the previous one
     */
    @Override
    public IActionChain after(int delay, String name, Consumer<IAction> task) {
        offset += delay;
        Consumer<IAction> wrapper = act -> {
            task.accept(act);
            act.markDone();
        };
        IAction a = scriptedActionManager.create(name, offset, wrapper);
        index++;
        a.setUpdateEveryXTick(1);
        scriptedActionManager.scheduleAction(a);
        return this;
    }

    @Override
    public IActionChain after(int delay, Consumer<IAction> task) {
        return after(delay, "chain#" + index, task);
    }
}
