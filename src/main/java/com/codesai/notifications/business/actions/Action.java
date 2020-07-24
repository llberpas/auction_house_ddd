package com.codesai.notifications.business.actions;


import com.codesai.notifications.business.actions.command.Command;

public interface Action<T extends Command> {
    void execute(T c);
}
